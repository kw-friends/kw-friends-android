package hello.kwfriends.datastoreManager

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserDataStore(context: Context) {
    init { pref = context.dataStore }

    companion object{
        val Context.dataStore : DataStore<Preferences> by preferencesDataStore("USER_DATA")
        lateinit var pref: DataStore<Preferences>
        //String 데이터 저장

        suspend fun setStringData(key: String, value: String){
            Log.w("Lim", "[pref] 데이터를 저장합니다. $key : $value")
            pref.edit {
                val prefKey = stringPreferencesKey(key)
                it[prefKey] = value
            }
        }

        //Boolean 데이터 저장
        suspend fun setBooleanData(key: String, value: Boolean){
            Log.w("Lim", "[pref] 데이터를 저장합니다. $key : $value")
            pref.edit {
                val prefKey = booleanPreferencesKey(key)
                it[prefKey] = value
            }
        }

        //key값에 해당하는 데이터 String Flow 받아오기
        fun getStringData(key: String): Flow<String> {
            Log.w("Lim", "[pref] 데이터를 불러옵니다. key = $key")
            val prefKey = stringPreferencesKey(key)
            return pref.data
                .map{
                    it[prefKey]?:""
                }
        }

        //데이터 Preferences Flow 받아오기
        fun getDataFlow(): Flow<Preferences> {
            Log.w("Lim", "[pref] 데이터를 불러옵니다.")
            return pref.data
        }

    }
}