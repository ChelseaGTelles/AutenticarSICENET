package com.wiz.sice.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.wiz.sice.data.AccesoLoginRequest
import com.wiz.sice.data.local.SicenetDatabase
import com.wiz.sice.data.repository.LocalRepository
import com.wiz.sice.data.repository.SicenetRepository

class FetchSicenetWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val type = inputData.getString("type") ?: return Result.failure()
        val repository = SicenetRepository()
        val localRepository = LocalRepository(SicenetDatabase.getDatabase(applicationContext))

        return try {
            val res = when (type) {
                "PROFILE" -> {
                    val session = localRepository.getSession() ?: return Result.failure()
                    val loginRes = repository.accesoLogin(AccesoLoginRequest(session.matricula, session.contrasenia, session.tipoUsuario))
                    if (loginRes.isSuccess) {
                        repository.getAlumno().getOrNull()?.rawJson
                    } else null
                }
                "CARGA" -> "" 
                "KARDEX" -> "" 
                else -> null
            }
            
            Result.success(workDataOf("content" to res, "type" to type))
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

class SaveLocalWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val content = inputData.getString("content") ?: return Result.failure()
        val type = inputData.getString("type") ?: return Result.failure()
        val localRepository = LocalRepository(SicenetDatabase.getDatabase(applicationContext))

        return try {
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
