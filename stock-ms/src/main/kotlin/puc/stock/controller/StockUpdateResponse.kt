package puc.stock.controller

data class StockUpdateResponse(
    val id: Long,
    val productId: String,
    var quantity: Int
)
