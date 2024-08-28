package puc.stock.service.impl

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import puc.stock.controller.request.StockUpdateRequest
import puc.stock.exception.NotEnoughStockException
import puc.stock.exception.ProductAlreadyExistsException
import puc.stock.exception.ProductNotFoundException
import puc.stock.model.Stock
import puc.stock.repository.StockRepository

@ExtendWith(SpringExtension::class)
class StockServiceImplTest {
    private val repository: StockRepository = mockk()
    private val stockService = StockServiceImpl(repository)

    @Test
    fun `should write down stock of product with success`() {
        // Given
        val request = StockUpdateRequest("1", 1)
        val stock = Stock(1, "1", 1)

        every { repository.findByProductId(request.productId!!) } returns stock
        every { repository.save(stock) } returns stock

        // When
        val actualStock = stockService.writeDownStock(request).body

        // Then
        assertNotNull(actualStock)
        assertEquals(0, actualStock!!.quantity)
        verify { repository.findByProductId(request.productId!!) }
        verify { repository.save(stock) }
    }

    @Test
    fun `should fail write down stock if product not found`() {
        // Given
        val request = StockUpdateRequest("1", 1)

        every { repository.findByProductId(request.productId!!) } returns null

        // Then
        val exception = assertThrows(ProductNotFoundException::class.java) {
            stockService.writeDownStock(request)
        }

        assertEquals("Produto com id [1] não encontrado", exception.message)
        verify { repository.findByProductId(request.productId!!) }
    }

    @Test
    fun `should fail write down stock if product not have enough quantity`() {
        // Given
        val request = StockUpdateRequest("1", 1)
        val stock = Stock(1, "1", 0)

        every { repository.findByProductId(request.productId!!) } returns stock

        // Then
        val exception = assertThrows(NotEnoughStockException::class.java) {
            stockService.writeDownStock(request)
        }

        assertEquals("Produto com id [1] não tem estoque suficiente", exception.message)
        verify { repository.findByProductId(request.productId!!) }
    }

    @Test
    fun `should create stock for a product with success`() {
        // Given
        val request = StockUpdateRequest("1", 1)
        val stock = Stock(null, "1", 1)

        every { repository.findByProductId(request.productId!!) } returns null
        every { repository.save(stock) } returns Stock(1, "1", 1)

        // When
        val actualStock = stockService.addProductStock(request).body

        // Then
        assertNotNull(actualStock)
        verify { repository.findByProductId(request.productId!!) }
        verify { repository.save(stock) }
    }

    @Test
    fun `should fail create stock for a product if a stock already exists `() {
        // Given
        val request = StockUpdateRequest("1", 1)
        val stock = Stock(1, "1", 1)

        every { repository.findByProductId(request.productId!!) } returns stock
        every { repository.save(stock) } returns Stock(1, "1", 1)

        // Then
        val exception = assertThrows(ProductAlreadyExistsException::class.java) {
            stockService.addProductStock(request)
        }

        assertEquals("Estoque do produto com id [1] já está cadastrado", exception.message)
        verify { repository.findByProductId(request.productId!!) }
    }
}