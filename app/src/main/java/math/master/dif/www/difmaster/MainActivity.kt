package math.master.dif.www.difmaster

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    var ask_for_send = ""
    var old_ask = ""

    fun ToSend (v: View) {
        if(old_ask != enter_ans.text.toString() && enter_ans.text.toString() != "") {
            request_text.text = "Решается..."
            old_ask = enter_ans.text.toString()

            val sPref = getSharedPreferences("math.master.dif.www.difmaster", MODE_PRIVATE)
            val editor = sPref.edit()
            editor.remove("answer")
            editor.remove("graph")
            editor.apply()

            graphic.loadDataWithBaseURL("file:///android_asset/", "", "text/html", "UTF-8", null);

            ask_for_send = enter_ans.text.toString()
            ask_for_send = replaceSim(ask_for_send, "'", "%27")
            ask_for_send = replaceSim(ask_for_send, "+", "%2B")
            ask_for_send = replaceSim(ask_for_send, "=", "%3D")
            ask_for_send = replaceSim(ask_for_send, "^", "%5E")
            val send = SendRequest(this)
            send.execute(
                "result",
                "https://www.wolframalpha.com/widget/input/?input=%5B%2F%2Fmath%3A$ask_for_send%2F%2F%5D&id=99268daa1041f8111962c8ff576dd5c8&includepodid=Limit&includepodid=DefiniteIntegral&includepodid=Result&includepodid=DifferentialEquationSolution&includepodid=Input&includepodid=IndefiniteIntegral"
            )
            WaitResult.sendEmptyMessageDelayed(0, 1000L)
        }
    }

    fun replaceSim (for_replace: String, sim: String, new_sim: String):String {
        var str_array = for_replace.split(sim)
        var ans_fun = ""
        for((i, str) in str_array.withIndex()) {
            if(i == 0) {
                ans_fun += str
            } else {
                ans_fun += new_sim + str
            }
        }
        return ans_fun
    }

    val Con = this

    private val WaitResult = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(message: Message) {
            val sPref = getSharedPreferences("math.master.dif.www.difmaster", Context.MODE_PRIVATE)
            val answer = sPref.getString("answer", "")

            if(answer != "") {
                var req = ""
                val check = answer.split("""id="i_0200_1""")
                if(check.size == 1) {
                    request_text.text = "Решение не найдено"
                } else {
                    req = check[1]
                    req = req.split("""data""")[0]
                    req = req.split("""title="""")[1]
                    req = req.split(""""""")[0]
                    request_text.text = req

                    var for_graph = req.split(" = ")[1]
                    for_graph = replaceSim(for_graph, " ", "")
                    for(i in 1..6) {
                        for_graph = replaceSim(for_graph, "c_"+i, "1")
                    }
                    for_graph = replaceSim(for_graph, "+", "%2B")
                    for_graph = replaceSim(for_graph, "=", "%3D")
                    for_graph = replaceSim(for_graph, "^", "%5E")

                    val send = SendRequest(Con)
                    send.execute("graph",
                        "http://old.yotx.ru/default.aspx?clr0=000000&exp0=$for_graph&mix=-10&max=10&asx=on&u=mm&nx=X&aiy=on&asy=on&ny=Y&iw=400&ih=500&ict=png&aa=on")

                    WaitGraph.sendEmptyMessageDelayed(0, 1000L)
                }
            } else {
                this.sendEmptyMessageDelayed(0, 1000L)
            }
        }
    }

    private val WaitGraph = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(message: Message) {
            val sPref = getSharedPreferences("math.master.dif.www.difmaster", Context.MODE_PRIVATE)
            val answer = sPref.getString("graph", "")

            if(answer != "") {
                var req = answer
                val check = answer.split("""ctl00_MainContentHolder_mGraphBlock""")
                if(check.size != 1) {
                    req = check[1]
                    req = req.split("""</div>""")[0]
                    req = req.split("""">""")[1]

                    var image = req.split("Graph.ashx")
                    req = image[0] + "http://old.yotx.ru/Graph.ashx" + image[1]

                    image = req.split("/>")
                    req = image[0] + """style="width:100%;height:auto;"/>"""

                    graphic.loadDataWithBaseURL("file:///android_asset/", req, "text/html", "UTF-8", null);
                } else {
                    graphic.loadDataWithBaseURL("file:///android_asset/", "<p>Не удалось построить график</p>", "text/html", "UTF-8", null);
                }
            } else {
                this.sendEmptyMessageDelayed(0, 1000L)
            }
        }
    }
}
