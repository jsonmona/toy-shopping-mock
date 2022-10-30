package kr.co.minary.shoppingmock

class ProductList(private val state: AppState) {
    private val productIdList: ArrayList<Long> = ArrayList()

    private var initThread = Thread {
        var lastId: Long? = null
        while (true) {
            val list = try {
                state.listProducts(lastId)
            } catch (_: IllegalStateException) {
                // 앱이 종료되는중
                return@Thread
            }

            if (list.size == 0)
                break

            productIdList.addAll(list)
            lastId = list.last()
        }
    }

    init {
        initThread.start()
    }

    fun length(): Int {
        initThread.join()
        return productIdList.size
    }

    fun get(idx: Int): ProductInfo {
        initThread.join()
        return state.getProduct(productIdList[idx])!!
    }
}
