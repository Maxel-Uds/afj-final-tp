package puc.stock.controller.response

data class StockResponse(
    val id: Long,
    val productId: String,
    var quantity: Int
)
