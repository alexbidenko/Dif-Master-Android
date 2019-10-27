package math.master.dif.www.difmaster

import android.content.Context
import android.os.AsyncTask
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.util.ArrayList


internal class SendRequest(val Con: Context) : AsyncTask<String, Void, String>() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        val http: HttpPost

        val httpclient = DefaultHttpClient()
        http = HttpPost(params[1])

        val response = httpclient.execute(http, BasicResponseHandler()) as String

        val sPref = Con.getSharedPreferences("math.master.dif.www.difmaster", Context.MODE_PRIVATE)
        val editor = sPref.edit()
        if(params[0] == "result") {
            editor.putString("answer", response)
        } else {
            editor.putString("graph", response)
        }
        editor.apply()

        return response
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
    }
}