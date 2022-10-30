package kr.co.minary.shoppingmock

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.core.database.sqlite.transaction


class DatabaseHelper(
    context: Context,
    name: String?,
    version: Int
) : SQLiteOpenHelper(context, name, null, version) {
    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)

        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!

        db.execSQL("CREATE TABLE user(" +
                "user_id INTEGER PRIMARY KEY," +
                "login_id TEXT NOT NULL UNIQUE," +
                "login_pw_hash TEXT NOT NULL," +
                "name TEXT," +
                "phone TEXT," +
                "address TEXT)")

        db.execSQL("CREATE TABLE config(" +
                "name TEXT NOT NULL UNIQUE," +
                "value TEXT)")

        db.execSQL("CREATE TABLE product(" +
                "product_id INTEGER PRIMARY KEY," +
                "title TEXT," +
                "price INTEGER," +
                "thumbnail_uri TEXT," +
                "thumbnail_resource TEXT)")

        insertStockProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != 1) {
            throw NotImplementedError("Unknown DB revision: $oldVersion")
        }
        if (newVersion != 1) {
            throw NotImplementedError("Unknown DB revision: $newVersion")
        }

        // 버전 같음
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!

        db.execSQL("DROP TABLE IF EXISTS user")
        db.execSQL("DROP TABLE IF EXISTS config")
        db.execSQL("DROP TABLE IF EXISTS product")
        onCreate(db)
    }

    private fun insertStockProducts(db: SQLiteDatabase) {
        // https://stackoverflow.com/a/5009740/4439606
        db.transaction(true) {
            execSQL("INSERT INTO product (title, price, thumbnail_resource) VALUES ('갤럭시 갤라즈 GALAX 지포스 RTX 4090 SG OC D6X 24GB', 2852000, 'gpu_a')")
            execSQL("INSERT INTO product (title, price, thumbnail_resource) VALUES ('MSI 지포스 RTX 4090 슈프림 X D6X 24GB 트라이프로져3S', 3189920, 'gpu_b')")
            execSQL("INSERT INTO product (title, price, thumbnail_resource) VALUES ('COLORFUL 지포스 RTX 4090 토마호크 EX D6X 24GB', 2589610, 'gpu_c')")
            execSQL("INSERT INTO product (title, price, thumbnail_resource) VALUES ('ASUS ROG STRIX 지포스 RTX 4090 O24G GAMING OC D6X 24GB', 2853506, 'gpu_d')")
            execSQL("INSERT INTO product (title, price, thumbnail_resource) VALUES ('이엠텍 지포스 RTX 4090 GAMEROCK D6X 24GB', 2778000, 'gpu_e')")
            execSQL("INSERT INTO product (title, price, thumbnail_resource) VALUES ('GIGABYTE 지포스 RTX 4090 Gaming OC D6X 24GB 피씨디렉트', 2858000, 'gpu_f')")
        }
    }
}