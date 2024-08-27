package puc.stock.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import puc.stock.controller.request.StockUpdateRequest
import puc.stock.service.StockService

@Validated
@RestController
@RequestMapping("/stock")
class StockController(val stockService: StockService) {

    @PatchMapping("/write-down")
    @ResponseStatus(HttpStatus.OK)
    fun writeDown(@Valid @RequestBody stockUpdateRequest: StockUpdateRequest) : ResponseEntity<StockUpdateResponse> {
        return stockService.writeDownStock(stockUpdateRequest)
    }
}
