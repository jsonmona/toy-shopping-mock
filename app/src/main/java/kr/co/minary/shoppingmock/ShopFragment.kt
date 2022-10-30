package kr.co.minary.shoppingmock

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShopFragment : Fragment(R.layout.fragment_shop) {
    private lateinit var state: AppState
    private lateinit var toolbar: Toolbar
    private lateinit var products: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        state = AppState.getInstance(requireContext())
        toolbar = view.findViewById(R.id.toolbar)
        products = view.findViewById(R.id.shop_products)

        products.adapter = ProductAdapter(requireContext(), ProductList(state))
        products.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()

        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
    }
}