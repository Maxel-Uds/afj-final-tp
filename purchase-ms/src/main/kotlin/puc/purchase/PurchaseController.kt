package puc.purchase

import org.springframework.amqp.AmqpException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.fasterxml.jackson.core.JsonProcessingException
import puc.model.PurchaseRequest
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import puc.service.PurchaseService
import puc.util.JwtUtil
import jakarta.validation.Valid

@Serializable
data class ErrorGateway(val timestamp: String, val status: Int, val error: String, val path: String)

@RestController
@RequestMapping("/purchase")
class PurchaseController(val purchaseService: PurchaseService, val jwtUtil: JwtUtil) {

    @PostMapping("/buy")
    fun buy(@RequestHeader("Authorization") token: String, @Valid @RequestBody purchaseRequest: PurchaseRequest): ResponseEntity<String> {
        try {
            val userId = jwtUtil.getUserId(token)
            purchaseService.sendMessage(purchaseRequest, userId)
        }catch (e: AmqpException) {
            return ResponseEntity.internalServerError().body("Failed to send message to MQ");
        } catch (e: JsonProcessingException) {
            return ResponseEntity.internalServerError().body("Failed to process json");
        } catch (e: RuntimeException) {
            val errorMsg = e.message
            if(errorMsg == null){
                return ResponseEntity.internalServerError().body("Body of error is empty");
            }
            val jsonStartIndex = errorMsg.indexOf('{')
            val jsonEndIndex = errorMsg.indexOf('}', jsonStartIndex)

            val jsonString = errorMsg.substring(jsonStartIndex..jsonEndIndex)

            val errorJson = Json.decodeFromString<ErrorGateway>(jsonString)
            val errorResponseJson = Json.encodeToString(errorJson)
            return ResponseEntity.status(errorJson.status).body(errorResponseJson);
        }

        return ResponseEntity.ok("Purchase request sent.") }
}