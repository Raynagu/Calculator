package com.mendtrix.gstcalculator

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import java.net.URI
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    var isEqual = true
    var result = 0.0
    var shareResult = ""
    var num = "0"
    var total = ""
    var df: DecimalFormat = DecimalFormat("##,##,###.#######")
    private val dataSet: MutableList<String> = arrayListOf()
    private lateinit var listAdapter: InputListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listAdapter = InputListAdapter(
            this,
            R.layout.item_view,
            dataSet
        )
        tv_result.adapter = listAdapter
        reset()
        //Numbers
        btnOne.setOnClickListener { inputValue("1", true) }
        btnTwo.setOnClickListener { inputValue("2", true) }
        btnThree.setOnClickListener { inputValue("3", true) }
        btnFour.setOnClickListener { inputValue("4", true) }
        btnFive.setOnClickListener { inputValue("5", true) }
        btnSix.setOnClickListener { inputValue("6", true) }
        btnSeven.setOnClickListener { inputValue("7", true) }
        btnEight.setOnClickListener { inputValue("8", true) }
        btnNine.setOnClickListener { inputValue("9", true) }
        btnZero.setOnClickListener { inputValue("0", true) }
        btnDoubleZero.setOnClickListener {
            val text = num
            if (text != "0" && (text.last().isDigit() || text.last() == '.') && !text.contains('='))
                inputValue("00", true)
            else
                inputValue("0", true)
        }
        btnPoint.setOnClickListener {
            val text = num
            if (!text.contains('.')) {
                if (text != "0" && text.last().isDigit() && !text.contains('='))
                    inputValue(".", true)
                else
                    inputValue("0.", true)
            }
        }

        //Operators
        btnAdd.setOnClickListener { inputValue("+ ", false) }
        btnSubtract.setOnClickListener { inputValue("- ", false) }
        btnMultiply.setOnClickListener { inputValue("× ", false) }
        btnDivide.setOnClickListener { inputValue("÷ ", false) }
        btnPercentage.setOnClickListener {
            if (!num.first().isDigit() && !isEqual && num.last() != ' ') {
                val exp = num.split(" ")
                var perc = result * (exp[1].toDouble() / 100)
                when (exp[0]) {
                    "+" -> result += perc
                    "-" -> result -= perc
                    "×" -> {
                        perc = exp[1].toDouble() / 100
                        result *= perc
                    }
                    "÷" -> {
                        perc = exp[1].toDouble() / 100
                        result /= perc
                    }
                }
                total = "= ${result.format()}"
                addItem("${num.getFormat()}% = ${perc.format().getFormat()}")
                addItem(total.getFormat())
                num = total
                tv_input.text = num.getFormat()
                isEqual = true
            }
        }
        btnEqual.setOnClickListener {
            if (!isEqual && num.last() != ' ') {
                showResult()
            }
            //update GrossTotal value
        }

        //display managers
        btnClear.setOnClickListener {
            reset()
        }
        btnRemove.setOnClickListener {
            var text = num
            if ((text != "0" && text.last().isDigit()) || text.last() == '.') {
                text = text.dropLast(1)
            }
            if (text.isEmpty() || text.contains('=')) {
                text = "0"
                result = 0.0
                isEqual = true
            }
            num = text
            tv_input.text = num.getFormat()
        }

        btn_more.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet, null)
            dialog.setContentView(view)
            dialog.show()
            val version = "v" + BuildConfig.VERSION_NAME
            view.tv_version.text = version
            view.btn_share.setOnClickListener {
                val shareData = getString(R.string.app_description) +" "+ getString(R.string.app_url)
               share(shareData)
            }
            view.btn_rate.setOnClickListener {
                val uri = Uri.parse("market://details?id=com.mendtrix.gstcalculator")
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }catch (e:ActivityNotFoundException){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url))))
                }
            }
        }
        btn_copy.setOnClickListener {
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoard.setPrimaryClip(ClipData.newPlainText("Calculations", shareResult+"\nDownload GST Calculator app "+getString(R.string.app_url)))

            Toast.makeText(this, "Copied to clipboard.", Toast.LENGTH_SHORT).show()
        }
        btn_send.setOnClickListener { share(shareResult+"\nDownload GST Calculator app "+getString(R.string.app_url))}
    }

    private fun share(data: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share via:"))
    }


    fun gstButton(view: View) {
        val text = num
        if (text != "0" && text.last() != ' ') {
            if (!text.first().isDigit() && text.last().isDigit() && !isEqual) {
                showResult()
            } else if (!text.contains('=')) {
                if (text.isNotEmpty() && text.first().isDigit()) {
                    addItem(" ")
                    shareResult = ""
                }
                addItem(text.getFormat())
            }
            when (view.id) {
                R.id.btnGst3 -> calculateGST(3)
                R.id.btnGst5 -> calculateGST(5)
                R.id.btnGst12 -> calculateGST(12)
                R.id.btnGst18 -> calculateGST(18)
                R.id.btnGst28 -> calculateGST(28)
                R.id.btnGst_3 -> calculateGST(3, false)
                R.id.btnGst_5 -> calculateGST(5, false)
                R.id.btnGst_12 -> calculateGST(12, false)
                R.id.btnGst_18 -> calculateGST(18, false)
                R.id.btnGst_28 -> calculateGST(28, false)
            }
        }
    }

    private fun showResult() {
        calculateExp()
        addItem(num.getFormat())

        num = "= ${result.format()}"
        total = num.getFormat()
        addItem(total)
        tv_input.text = num.getFormat()
        isEqual = true
    }

    private fun calculateGST(i: Int, isAdd: Boolean = true) {
        val text = num
        val cost: Double = if (text.contains('='))
            text.split(' ')[1].toDouble()
        else
            text.toDouble()
        val gstAmt: Double
        val netPrice: String
        if (isAdd) {
            gstAmt = (cost * i) / 100
            netPrice = "= ${(cost + gstAmt).format()}"
        } else {
            gstAmt = cost - (cost * 100 / (100 + i))
            netPrice = "= ${(cost - gstAmt).format()}"
        }

        val cGstRate = i.toDouble() / 2
        val cGstAmt = (gstAmt / 2).format().getFormat()
        val sign = if (isAdd) "+" else "-"

        val showDisp =
            "$sign GST $i% = ${gstAmt.format().getFormat()}\nCGST $cGstRate% = $cGstAmt\n" +
                    "SGST $cGstRate% = $cGstAmt"
        addItem(showDisp)
        addItem(netPrice.getFormat())
        num = netPrice
        tv_input.text = num.getFormat()
        result = netPrice.split(' ')[1].toDouble()
        isEqual = true
    }

    private fun reset() {
        dataSet.clear()
        listAdapter.notifyDataSetChanged()
        tv_input.text = "0"
        shareResult = ""
        num = "0"
        result = 0.0
        isEqual = true
    }

    private fun inputValue(input: String, isNum: Boolean) {
        if (isNum) {
            if (num.length > 14) {
                return
            }
            if (num == "0" || num.contains('=')) {
                num = input
            } else {
                num += input
            }
            tv_input.text = num.getFormat()
        } else {
            if (num.last().isWhitespace()) {
                num = input
                tv_input.text = num
                return
            }
            if (dataSet.isEmpty()) {
                addItem(num.getFormat())
                result = num.toDouble()
            } else if (isEqual && num.first().isDigit()) {
                addItem("\n" + num.getFormat())
                shareResult = num.getFormat()
                result = num.toDouble()

            } else {
                calculateExp()
                if (!isEqual) {
                    addItem(num.getFormat())
                }
            }
            num = input
            tv_input.text = num
            isEqual = false
        }
    }

    private fun addItem(item: String) {
        listAdapter.add(item)
        shareResult +="\n"+item
    }

    private fun calculateExp() {
        val exp = num.split(" ")
        when (exp[0]) {
            "+" -> result += exp[1].toDouble()
            "-" -> result -= exp[1].toDouble()
            "×" -> result *= exp[1].toDouble()
            "÷" -> result /= exp[1].toDouble()
        }
    }

    private fun Double.format(digits: Int = 3) = java.lang.String.format("%.${digits}f", this)

    private fun String.getFormat(): String {
        return if (contains(' ') && last().isDigit()) {
            val arr = split(' ')
            "${arr[0]} " + df.format(arr[1].toDouble())
        } else if (last().isDigit()) {
            df.format(this.toDouble())
        } else {
            this
        }
    }
}

