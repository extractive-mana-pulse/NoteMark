import android.net.http.HttpException
import android.os.Build
import android.util.Log
import android.util.Log.e
import androidx.annotation.RequiresExtension
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.data.local.NoteDatabase
import com.example.data.remote.toEntity
import com.example.domain.NoteEntity
import com.example.domain.NoteService
import kotlinx.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NoteRemoteMediator(
    private val noteDatabase: NoteDatabase,
    private val noteApi: NoteService
) : RemoteMediator<Int, NoteEntity>() {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, NoteEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    state.lastItemOrNull() ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    // Calculate the next page based on current items count
                    (noteDatabase.dao.getNotesCount() / state.config.pageSize) + 1
                }
            }
            val notesResponses = noteApi.getNotes(
                page = loadKey,
                size = state.config.pageSize
            )

            noteDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    noteDatabase.dao.clearAll()
                }
                // Convert NotesResponse to List<NoteEntity>
                val noteEntities = notesResponses.notes.map { it.toEntity() }
                noteDatabase.dao.upsertAll(noteEntities)
            }

            return MediatorResult.Success(
                endOfPaginationReached = notesResponses.notes.isEmpty()
            )

        } catch (e: IOException) {
            e("NoteRemoteMediator", "Network error: ${e.message}")
            val hasLocalData = noteDatabase.dao.getNotesCount() > 0
            if (hasLocalData && loadType == LoadType.REFRESH) {
                Log.d("NoteRemoteMediator", "Using cached data due to network error")
                MediatorResult.Success(endOfPaginationReached = false)
            } else {
                MediatorResult.Error(e)
            }
        } catch (e: HttpException) {
            e("NoteRemoteMediator", "HTTP error: ${e.message}")
            val hasLocalData = noteDatabase.dao.getNotesCount() > 0
            if (hasLocalData && loadType == LoadType.REFRESH) {
                Log.d("NoteRemoteMediator", "Using cached data due to HTTP error")
                MediatorResult.Success(endOfPaginationReached = false)
            } else {
                MediatorResult.Error(e)
            }
        } catch (e: Exception) {
            e("NoteRemoteMediator", "Unknown error: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}