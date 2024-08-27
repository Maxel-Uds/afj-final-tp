package puc.stock.service.impl

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import puc.stock.controller.response.StockUpdateResponse
import puc.stock.controller.request.StockUpdateRequest
import puc.stock.exception.NotEnoughStockException
import puc.stock.exception.ProductAlreadyExistsException
import puc.stock.exception.ProductNotFoundException
import puc.stock.repository.StockRepository
import puc.stock.service.StockService
import puc.stock.model.Stock

@Service
class StockServiceImpl(val stockRepository: StockRepository) : StockService {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun writeDownStock(stockUpdateRequest: StockUpdateRequest) : ResponseEntity<StockUpdateResponse> {
        val stock = stockRepository.findByProductId(stockUpdateRequest.productId!!)
            ?: throw ProductNotFoundException(String.format("Produto com id [%s] não encontrado", stockUpdateRequest.productId))

        if (stock.quantity < stockUpdateRequest.quantity!!) {
            logger.error("=== Erro, produto [{}] com estoque insuficiente", stockUpdateRequest.productId)
            throw NotEnoughStockException(String.format("Produto com id [%s] não tem estoque suficiente", stockUpdateRequest.productId))
        }

        stock.quantity -= stockUpdateRequest.quantity

        logger.info("=== Salvando atualização de estoque [{}]", stock.toString())
        val stockUpdated = stockRepository.save(stock)

        logger.info("=== Estoque atualizado com sucesso, quantidade atual [{}]", stockUpdated.quantity)
        return ResponseEntity.ok(StockUpdateResponse(stockUpdated.id!!, stockUpdated.productId, stockUpdated.quantity))
    }

    @Transactional
    override fun addProductStock(stockUpdateRequest: StockUpdateRequest) : ResponseEntity<StockUpdateResponse> {
        val existingStock = stockRepository.findByProductId(stockUpdateRequest.productId!!)

        if (existingStock != null) {
            logger.error("=== Erro, o produto [{}] já existe no estoque", stockUpdateRequest.productId)
            throw ProductAlreadyExistsException(String.format("Produto com id [%s] já existente", stockUpdateRequest.productId))
        }

        val stock = Stock(
            productId = stockUpdateRequest.productId,
            quantity = stockUpdateRequest.quantity!!
        )

        logger.info("=== Salvando estoque [{}]", stock.toString())
        val stockSave = stockRepository.save(stock)

        logger.info("=== Estoque [{}] salvo", stockSave.toString())
        return ResponseEntity.ok(StockUpdateResponse(stockSave.id!!, stockSave.productId, stockSave.quantity))
    }
}
