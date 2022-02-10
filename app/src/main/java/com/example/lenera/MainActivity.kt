package com.example.lenera
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import java.io.BufferedReader
import java.io.File

import android.view.View
import java.lang.StringBuilder

fun getNotZeroLong(value: Long) : Long {
    if (value != 0L)
        return value
    else
        return 1
}

fun getStateString(wordsLearned: Int, fileDate: File) : String{
    return "words: " + wordsLearned.toString() + " | avg per day: " + (wordsLearned.toLong() / getNotZeroLong(((System.currentTimeMillis() - fileDate.lastModified()) / 1000 / 60 / 60 / 24))).toString()
}

fun getListNumOf(list : ArrayList<Int>, target: Int) : Int {
    var result = 0
    for (elem in list)
        if (elem == target)
            result += 1
    return result
}

interface OnScrollChangeListener {
    fun onScrollChange(
        v: View,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    )
}
class MainActivity : AppCompatActivity(), com.example.lenera.OnScrollChangeListener {
    override fun onScrollChange(v: View,
                                scrollX: Int,
                                scrollY: Int,
                                oldScrollX: Int,
                                oldScrollY: Int) {
        Log.d("TAGG", "Scroll")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_main)

        // INITIALIZATION
        val scrollBox = findViewById<LinearLayout>(R.id.ScrollBox)
        val buttonStat = findViewById<Button>(R.id.buttonStat)


        val dicEn = (BufferedReader(assets.open("Dictionaries/Dic1/en.txt").reader()).readText()).lines()
        val dicRu = (BufferedReader(assets.open("Dictionaries/Dic1/ru.txt").reader()).readText()).lines()
        val fileDate = File(applicationContext.filesDir,"fileDate")
        if (!fileDate.exists())
            fileDate.createNewFile()
        val fileButtonsData = File(applicationContext.filesDir, "ButtonsData.txt")
        val buttonsState: ArrayList<Int> = ArrayList()
        if (!(fileButtonsData.exists())) {
            val stringButtonsData = StringBuilder()
            for (index in dicEn.indices) {
                buttonsState.add(0)
                stringButtonsData.append(buttonsState[index].toString() + "\n")
            }
            fileButtonsData.createNewFile()
            fileButtonsData.writeBytes(stringButtonsData.toString().toByteArray())
        } else {

            val dataFromFile = fileButtonsData.reader().readText().lines()
            for (i in dataFromFile) {
                if (i.isNotEmpty()) {
                    if (i[0] == '0')
                        buttonsState.add(0)
                    else
                        buttonsState.add(1)
                }
            }
        }
        // FILE END

        // colors
        val colorBackDefault = Color.parseColor("#ffde03")
        val colorBackPressed = Color.parseColor("#0336ff")
        val colorTextDefault = Color.parseColor("#000000")
        val colorTextPressed = Color.parseColor("#ffffff")

        //set stat button color
        buttonStat.setBackgroundColor(colorBackPressed)
        buttonStat.setTextColor(colorTextPressed)

        //dateNow.seconds
        buttonStat.setText(getStateString(getListNumOf(buttonsState, 1), fileDate))

        var scrollObject = findViewById<ScrollView>(R.id.BaseScrollView)
        //scrollObject.setOnScrollChangeListener(View.OnScrollChangeListener(onScrollChange())){

        //}


        for (index in dicEn.indices) {

            val button = Button(this)
            scrollBox.addView(button)
            button.setTag(Integer.toString(scrollBox.indexOfChild(button)))


            var buttonState = buttonsState[index] == 1

            if (buttonState) {
                button.setBackgroundColor(colorBackPressed)
                button.setTextColor(colorTextPressed)
                button.setText(dicEn[index] + "\t-\t" + dicRu[index])
            }
            else {
                button.setBackgroundColor(colorBackDefault)
                button.setTextColor(colorTextDefault)
                button.setText(dicEn[index])
            }




            button.setOnClickListener() {
                buttonState = buttonsState[index] == 1
                if (buttonState) {
                    button.setBackgroundColor(colorBackDefault)
                    button.setTextColor(colorTextDefault)
                    button.setText(dicEn[index])
                    buttonsState[index] = 0
                }
                else {
                    button.setBackgroundColor(colorBackPressed)
                    button.setTextColor(colorTextPressed)
                    button.setText(dicEn[index] + "\t-\t" + dicRu[index])
                    buttonsState[index] = 1
                }

                //update button state in file
                buttonStat.setText(getStateString(getListNumOf(buttonsState, 1), fileDate))
                fileButtonsData.delete()
                fileButtonsData.createNewFile()
                val stringButtonsData = StringBuilder()
                for (btnState in buttonsState) {
                    stringButtonsData.append(btnState.toString() + "\n".toString())
                }
                fileButtonsData.writeBytes(stringButtonsData.toString().toByteArray())
            }

                //if (index > 20)
                //break
        }

    }
}