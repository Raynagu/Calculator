package com.mendtrix.calculator

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    var isEqual = true
    var result = 0.0
    var num = "0"
    var total = ""
    var df: DecimalFormat = DecimalFormat("##,##,###.#######")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        reset()
        tv_result.movementMethod = ScrollingMovementMethod()
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
            if(text !="0" && (text.last().isDigit() || text.last() == '.') && !text.contains('='))
                inputValue("00", true)
            else
                inputValue("0", true)
        }
        btnPoint.setOnClickListener {
            val text = num
            if(!text.contains('.')) {
                if(text != "0" && text.last().isDigit() && !text.contains('='))
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
            if(!num.first().isDigit() && !isEqual && num.last() != ' '){
                val exp = num.split(" ")
                var perc = result*(exp[1].toDouble()/100)
                when(exp[0]){
                    "+" -> result += perc
                    "-" -> result -= perc
                    "×" -> {
                        perc = exp[1].toDouble()/100
                        result *= perc
                    }
                    "÷" -> {
                        perc = exp[1].toDouble()/100
                        result /= perc
                    }
                }
                total = "= ${result.format()}"
                tv_result.append("\n${num.getFormat()}% = ${perc.format().getFormat()}\n${total.getFormat()}")
                num = total
                tv_input.text = num.getFormat()
                isEqual = true
            }
        }
        btnEqual.setOnClickListener {
            if(!isEqual && num.last() != ' '){
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
            if((text !="0" && text.last().isDigit()) || text.last() == '.'){
                text = text.dropLast(1)
            }
            if(text.isEmpty() || text.contains('=')){
                text = "0"
                result = 0.0
                isEqual = true
            }
            num = text
            tv_input.text = num.getFormat()
        }
    }

    fun gstButton(view: View){
        val text = num
        if(text !="0" && text.last() != ' '){
            if(!text.first().isDigit() && text.last().isDigit() && !isEqual){
                showResult()
            }else if(!text.contains('=')){
                if(text.isNotEmpty() && text.first().isDigit()){
                    tv_result.append("\n")
                }
                tv_result.append("\n"+text.getFormat())
            }
            when(view.id){
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
        tv_result.append("\n"+num.getFormat())

        num = "= ${result.format()}"
        total = num.getFormat()
        tv_result.append("\n$total")
        tv_input.text = num.getFormat()
        isEqual = true
    }

    private fun calculateGST(i: Int, isAdd: Boolean = true) {
        val text = num
        val cost:Double = if(text.contains('='))
                                text.split(' ')[1].toDouble()
                           else
                                text.toDouble()
        val gstAmt: Double
        val netPrice: String
        if(isAdd){
            gstAmt = (cost*i)/100
            netPrice = "= ${(cost+gstAmt).format()}"
        }else{
            gstAmt = cost-(cost*100/(100+i))
            netPrice = "= ${(cost-gstAmt).format()}"
        }

        val cGstRate = i.toDouble()/2
        val cGstAmt = (gstAmt/2).format().getFormat()
        val sign = if(isAdd) "+" else "-"

        val showDisp = "$sign GST $i% = ${gstAmt.format().getFormat()}\nCGST $cGstRate% = $cGstAmt\n" +
                "SGST $cGstRate% = $cGstAmt\n${netPrice.getFormat()}"
        tv_result.append("\n$showDisp")
        num = netPrice
        tv_input.text = num.getFormat()
        result = netPrice.split(' ')[1].toDouble()
        isEqual = true
    }

    private fun reset() {
        tv_result.text = ""
        tv_input.text = "0"
        num = "0"
        result = 0.0
        isEqual = true
    }

    private fun inputValue(input: String, isNum: Boolean){
        if(isNum) {
            if(num == "0" || num.contains('=')) {
                num = input
            }else {
                num += input
            }
            tv_input.text = num.getFormat()
        }else {
            if(num.last().isWhitespace()){
                num = input
                tv_input.text = num
                return
            }
            if(tv_result.text.isEmpty()) {
                tv_result.text = num.getFormat()
                result = num.toDouble()
            }else if(isEqual && tv_input.text.first().isDigit()){
                tv_result.append("\n\n"+num.getFormat())
                result = num.toDouble()

            } else {
                calculateExp()
                if(!isEqual){
                    tv_result.append("\n"+num.getFormat())
                }
            }
            num = input
            tv_input.text = num
            isEqual = false
        }
    }

    private fun calculateExp() {
        val exp = num.split(" ")
        when(exp[0]){
            "+" -> result += exp[1].toDouble()
            "-" -> result -= exp[1].toDouble()
            "×" -> result *= exp[1].toDouble()
            "÷" -> result /= exp[1].toDouble()
        }
    }
    private fun Double.format(digits: Int = 2) = java.lang.String.format("%.${digits}f", this)

    private fun String.getFormat(): String {
        return if(contains(' ') && last().isDigit()){
            val arr = split(' ')
            "${arr[0]} "+ df.format(arr[1].toDouble())
        }else if(last().isDigit()){
            df.format(this.toDouble())
        }else{
            this
        }
    }
}

