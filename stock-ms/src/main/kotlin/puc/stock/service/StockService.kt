package puc.stock.service

import org.springframework.http.ResponseEntity
import puc.stock.controller.StockUpdateResponse
import puc.stock.controller.request.StockUpdateRequest

interface StockService {

    fun writeDownStock(stockUpdateRequest: StockUpdateRequest) : ResponseEntity<StockUpdateResponse>
}