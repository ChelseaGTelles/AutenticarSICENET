package com.wiz.sice.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.wiz.sice.data.local.SicenetDatabase
import com.wiz.sice.data.local.entities.CargaEntity
import com.wiz.sice.data.local.entities.KardexEntity

class SicenetProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.wiz.sice.provider"
        
        private const val CARGA = 1
        private const val CARGA_ID = 2
        private const val KARDEX = 3
        private const val KARDEX_ID = 4

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "carga_academica", CARGA)
            addURI(AUTHORITY, "carga_academica/#", CARGA_ID)
            addURI(AUTHORITY, "kardex", KARDEX)
            addURI(AUTHORITY, "kardex/#", KARDEX_ID)
        }
    }

    private lateinit var database: SicenetDatabase

    override fun onCreate(): Boolean {
        context?.let {
            database = SicenetDatabase.getDatabase(it)
            return true
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor: Cursor = when (uriMatcher.match(uri)) {
            CARGA -> database.cargaDao().getAllCargaCursor()
            KARDEX -> database.kardexDao().getAllKardexCursor()
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            CARGA -> "vnd.android.cursor.dir/$AUTHORITY.carga_academica"
            CARGA_ID -> "vnd.android.cursor.item/$AUTHORITY.carga_academica"
            KARDEX -> "vnd.android.cursor.dir/$AUTHORITY.kardex"
            KARDEX_ID -> "vnd.android.cursor.item/$AUTHORITY.kardex"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val id: Long = when (uriMatcher.match(uri)) {
            CARGA -> {
                val entity = CargaEntity(
                    materia = values?.getAsString("materia") ?: "",
                    grupo = values?.getAsString("grupo") ?: "",
                    docente = values?.getAsString("docente") ?: "",
                    creditosMateria = values?.getAsInteger("creditosMateria") ?: 0,
                    lunes = values?.getAsString("lunes") ?: "",
                    martes = values?.getAsString("martes") ?: "",
                    miercoles = values?.getAsString("miercoles") ?: "",
                    jueves = values?.getAsString("jueves") ?: "",
                    viernes = values?.getAsString("viernes") ?: ""
                )
                database.cargaDao().insert(entity)
            }
            KARDEX -> {
                val entity = KardexEntity(
                    clvOficial = values?.getAsString("clvOficial") ?: "",
                    materia = values?.getAsString("materia") ?: "",
                    periodo = values?.getAsString("periodo") ?: "",
                    promedio = values?.getAsString("promedio") ?: ""
                )
                database.kardexDao().insert(entity)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return ContentUris.withAppendedId(uri, id)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val count: Int = when (uriMatcher.match(uri)) {
            CARGA_ID -> {
                val id = ContentUris.parseId(uri)
                database.cargaDao().deleteById(id)
            }
            KARDEX_ID -> {
                val id = ContentUris.parseId(uri)
                database.kardexDao().deleteById(id)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {

        val count = when (uriMatcher.match(uri)) {

            CARGA_ID -> {

                val id = ContentUris.parseId(uri).toInt()
                val current = database.cargaDao().getById(id) ?: return 0

                val updated = current.copy(
                    materia = values?.getAsString("materia") ?: current.materia,
                    docente = values?.getAsString("docente") ?: current.docente
                )

                database.cargaDao().insert(updated)
                1
            }

            KARDEX_ID -> {

                val id = ContentUris.parseId(uri).toInt()
                val current = database.kardexDao().getById(id) ?: return 0

                val updated = current.copy(
                    materia = values?.getAsString("materia") ?: current.materia,
                    promedio = values?.getAsString("promedio") ?: current.promedio
                )

                database.kardexDao().insert(updated)
                1
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }

        return count
    }
}
