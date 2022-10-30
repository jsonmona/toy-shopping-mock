package kr.co.minary.shoppingmock

import android.R.attr
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import java.io.File
import java.security.MessageDigest


class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private lateinit var toolbar: Toolbar
    private lateinit var image: ImageView
    private lateinit var chooseImage: Button
    private lateinit var title: EditText
    private lateinit var price: EditText
    private lateinit var submit: Button
    private var copyThread: Thread? = null

    @Volatile
    private var imageName: String? = null

    companion object {
        private const val PICK_IMAGE: Int = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cleanUpTemporary()

        toolbar = view.findViewById(R.id.toolbar)
        image = view.findViewById(R.id.addproduct_image)
        chooseImage = view.findViewById(R.id.addproduct_choose_image)
        title = view.findViewById(R.id.addproduct_title)
        price = view.findViewById(R.id.addproduct_price)
        submit = view.findViewById(R.id.addproduct_submit)

        chooseImage.setOnClickListener {
            copyThread?.join()

            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")

            val chooserIntent = Intent.createChooser(getIntent, "")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)
        }

        submit.setOnClickListener {
            copyThread?.join()
            val context = context ?: return@setOnClickListener

            val title = title.text.toString()

            val price = try {
                price.text.toString().toLong()
            } catch (_: NumberFormatException) {
                Toast.makeText(context, R.string.addproduct_toast_wrong_price, Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val uri = imageName?.let {
                File(context.filesDir, it).toUri().toString()
            }

            if (title.isEmpty()) {
                Toast.makeText(context, R.string.addproduct_toast_empty_title, Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (price <= 0) {
                Toast.makeText(context, R.string.addproduct_toast_wrong_price, Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            AppState.getInstance(context).addProduct(
                ProductInfo(
                    title, price, uri, null
                )
            )

            (activity as? MainActivity)?.showShoppingPage()
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        refreshImage()
    }

    override fun onPause() {
        super.onPause()
        copyThread?.join()
        cleanUpTemporary()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val context = requireActivity()

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data?.data != null) {
            val input = try {
                context.contentResolver.openInputStream(data.data!!) ?: return
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, R.string.addproduct_image_import_fail, Toast.LENGTH_SHORT)
                    .show()
                return
            }

            copyThread = Thread {
                try {
                    input.use {
                        val md = MessageDigest.getInstance("SHA-256")
                        val buf = ByteArray(4096)
                        val path = File(context.filesDir, "imagecopytemp")
                        path.outputStream().use { output ->
                            while (true) {
                                val readLen = input.read(buf)
                                if (readLen <= 0)
                                    break
                                md.update(buf, 0, readLen)
                                output.write(buf, 0, readLen)
                            }
                        }
                        val digest = md.digest().joinToString("") { "%02x".format(it) }
                        println(digest)
                        val dst = File(context.filesDir, digest)
                        if (!path.renameTo(dst))
                            throw RuntimeException("rename failure")
                        imageName = digest
                        context.runOnUiThread {
                            refreshImage()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    context.runOnUiThread {
                        Toast.makeText(
                            context,
                            R.string.addproduct_image_import_fail,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.also { it.start() }
        }
    }

    private fun cleanUpTemporary() {
        val context = context ?: return
        File(context.filesDir, "imagecopytemp").let {
            if (it.exists())
                it.delete()
        }
    }

    @UiThread
    private fun refreshImage() {
        imageName.let { name ->
            if (name == null)
                image.setImageResource(R.drawable.no_photo)
            else
                image.setImageURI(File(requireContext().filesDir, name).toUri())
        }
    }
}
