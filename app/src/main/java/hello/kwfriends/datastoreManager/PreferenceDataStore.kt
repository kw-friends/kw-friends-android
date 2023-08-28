package hello.kwfriends.datastoreManager

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferenceDataStore(context: Context, datastoreName: String) {
    val Context.dataStore : DataStore<Preferences> by preferencesDataStore(datastoreName)
    var pref = context.dataStore

    //데이터 저장
    suspend fun setData(key: String, value: String){
        Log.w("Lim", "데이터를 저장합니다.")
        pref.edit {
            val prefKey = stringPreferencesKey(key)
            it[prefKey] = value
        }
    }

    //데이터 불러오기(IOException 등 발생 가능)
    fun getData(key: String): Flow<String> {
        Log.w("Lim", "데이터를 불러옵니다.")
        val prefKey = stringPreferencesKey(key)
        return pref.data
            .map{
                it[prefKey]?:""
            }
    }

}