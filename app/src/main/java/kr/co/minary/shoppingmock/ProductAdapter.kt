package kr.co.minary.shoppingmock

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val context: Context, private val list: ProductList) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val photo: ImageView
        val title: TextView
        val price: TextView

        init {
            photo = v.findViewById(R.id.shop_item_photo)
            title = v.findViewById(R.id.shop_item_title)
            price = v.findViewById(R.id.shop_item_price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = list.get(position)

        val uri: Uri?

        if (info.thumbnailResource != null) {
            val resId = context.resources.getIdentifier(
                info.thumbnailResource,
                "drawable",
                context.packageName
            )
            uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.resources.getResourcePackageName(resId))
                .appendPath(context.resources.getResourceTypeName(resId))
                .appendPath(context.resources.getResourceEntryName(resId))
                .build()
        } else if (info.thumbnailUri != null) {
            uri = Uri.parse(info.thumbnailUri) ?: null
        } else {
            uri = null
        }

        if (uri == null)
            holder.photo.setImageResource(R.drawable.no_photo)
        else
            holder.photo.setImageURI(uri)

        holder.title.text = info.title
        holder.price.text = "%,d 원".format(info.price)
    }

    override fun getItemCount(): Int {
        return list.length()
    }
}