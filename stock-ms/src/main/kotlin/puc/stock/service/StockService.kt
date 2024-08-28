package puc.stock.service

import puc.stock.controller.response.StockUpdateResponse
import puc.stock.controller.request.StockUpdateRequest
import puc.stock.controller.response.StockResponse

interface StockService {

    fun writeDownStock(stockUpdateRequest: StockUpdateRequest) : StockUpdateResponse

    fun addProductStock(stockUpdateRequest: StockUpdateRequest) : StockUpdateResponse

    fun getAllStock() : List<Stock>
}