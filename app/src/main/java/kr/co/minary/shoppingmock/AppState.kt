package kr.co.minary.shoppingmock

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.net.Uri
import androidx.core.database.getStringOrNull
import java.security.MessageDigest


class AppState private constructor(context: Context) {
    private val ctx: Context = context.applicationContext
    private val dbHelper: DatabaseHelper = DatabaseHelper(ctx, "database.db", 1)
    private val specialRegex: Regex = Regex("[\\\\\\[\\]`~!@#$%^&*()\\-_=+{};:'\"<>,.?/|]")

    private var currUserId: Long? = null

    companion object {
        // applicationContext를 저장하므로 괜찮음
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AppState? = null

        // https://stackoverflow.com/q/18093735/4439606
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: AppState(context).also { INSTANCE = it }
        }
    }

    private fun hashPassword(pw: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(pw.toByteArray(Charsets.US_ASCII))
        val digest = md.digest()
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun tryLogin(id: String, pw: String): Boolean {
        val userId: Long
        val storedPw: String

        dbHelper.readableDatabase.use { db ->
            db.rawQuery(
                "SELECT user_id, login_pw_hash FROM user WHERE login_id=? LIMIT 1",
                arrayOf(id)
            ).use { cursor ->
                if (!cursor.moveToNext())
                    return false

                userId = cursor.getLong(0)
                storedPw = cursor.getString(1)
            }
        }

        if (hashPassword(pw) != storedPw)
            return false

        currUserId = userId
        return true
    }

    fun logout() {
        currUserId = null
    }

    fun isLoggedIn(): Boolean {
        return currUserId != null
    }

    fun tryRegister(
        id: String,
        pw: String,
        name: String,
        phone: String,
        address: String
    ): RegisterResult {
        if (pw.length < 8 || !pw.contains(specialRegex))
            return RegisterResult.PASSWORD_TOO_WEAK

        val userId: Long

        try {
            dbHelper.writableDatabase.use { db ->
                userId = db.insertOrThrow("user", null, ContentValues().apply {
                    put("login_id", id)
                    put("login_pw_hash", hashPassword(pw))
                    put("name", name)
                    put("phone", phone)
                    put("address", address)
                })
            }
        } catch (_: SQLiteConstraintException) {
            return RegisterResult.ID_COLLISION
        } catch (e: SQLiteException) {
            e.printStackTrace()
            return RegisterResult.UNKNOWN
        }

        currUserId = userId

        return RegisterResult.OK
    }

    fun getLoginId(): String {
        if (!isLoggedIn())
            throw NotLoggedInException()

        dbHelper.readableDatabase.use { db ->
            db.rawQuery("SELECT login_id FROM user WHERE user_id=?", arrayOf(currUserId.toString()))
                .use { cursor ->
                    if (!cursor.moveToNext()) {
                        currUserId = null
                        throw NotLoggedInException()
                    }

                    return cursor.getString(0)
                }
        }
    }

    fun getName(): String {
        if (!isLoggedIn())
            throw NotLoggedInException()

        dbHelper.readableDatabase.use { db ->
            db.rawQuery("SELECT name FROM user WHERE user_id=?", arrayOf(currUserId.toString()))
                .use { cursor ->
                    if (!cursor.moveToNext()) {
                        currUserId = null
                        throw NotLoggedInException()
                    }

                    return cursor.getString(0)
                }
        }
    }

    fun getPhone(): String {
        if (!isLoggedIn())
            throw NotLoggedInException()

        dbHelper.readableDatabase.use { db ->
            db.rawQuery("SELECT phone FROM user WHERE user_id=?", arrayOf(currUserId.toString()))
                .use { cursor ->
                    if (!cursor.moveToNext()) {
                        currUserId = null
                        throw NotLoggedInException()
                    }

                    return cursor.getString(0)
                }
        }
    }

    fun getAddress(): String {
        if (!isLoggedIn())
            throw NotLoggedInException()

        dbHelper.readableDatabase.use { db ->
            db.rawQuery("SELECT address FROM user WHERE user_id=?", arrayOf(currUserId.toString()))
                .use { cursor ->
                    if (!cursor.moveToNext()) {
                        currUserId = null
                        throw NotLoggedInException()
                    }

                    return cursor.getString(0)
                }
        }
    }

    fun updateUser(newPassword: String?, newName: String?, newPhone: String?, newAddress: String?) {
        if (!isLoggedIn())
            throw NotLoggedInException()

        val values = ContentValues().apply {
            if (newPassword != null)
                put("login_pw_hash", hashPassword(newPassword))
            if (newName != null)
                put("name", newName)
            if (newPhone != null)
                put("phone", newPhone)
            if (newAddress != null)
                put("address", newAddress)
        }

        if (values.size() == 0)
            return


        dbHelper.writableDatabase.use { db ->
            db.update("user", values, "user_id=?", arrayOf(currUserId.toString()))
        }
    }

    fun listProducts(fromId: Long?): ArrayList<Long> {
        val result = ArrayList<Long>()

        dbHelper.readableDatabase.use { db ->
            val cursor = if (fromId != null) {
                db.rawQuery(
                    "SELECT product_id FROM product WHERE product_id < ? ORDER BY product_id DESC LIMIT 20",
                    arrayOf(fromId.toString())
                )
            } else {
                db.rawQuery(
                    "SELECT product_id FROM product ORDER BY product_id DESC LIMIT 20",
                    emptyArray()
                )
            }

            cursor.use {
                while (cursor.moveToNext()) {
                    result.add(cursor.getLong(0))
                }
            }
        }

        return result
    }

    fun getProduct(id: Long): ProductInfo? {
        dbHelper.readableDatabase.use { db ->
            db.rawQuery(
                "SELECT title, price, thumbnail_uri, thumbnail_resource FROM product WHERE product_id=?",
                arrayOf(id.toString())
            ).use { cursor ->
                if (!cursor.moveToNext())
                    return null

                return ProductInfo(
                    cursor.getString(0),
                    cursor.getLong(1),
                    cursor.getStringOrNull(2),
                    cursor.getStringOrNull(3)
                )
            }
        }
    }

    fun addProduct(item: ProductInfo): Long {
        dbHelper.writableDatabase.use { db ->
            return db.insert("product", null, ContentValues().apply {
                put("title", item.title)
                put("price", item.price)

                if (item.thumbnailUri != null)
                    put("thumbnail_uri", item.thumbnailUri)

                if (item.thumbnailResource != null)
                    put("thumbnail_resource", item.thumbnailResource)
            })
        }
    }
}
