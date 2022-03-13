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
import kotlin.coroutines.coroutineContext

fun getNotZeroLong(value: Long) : Long {
    if (value != 0L)
        return value
    else
        return 1
}

fun getStateString(wordsLearned: Int, date: Long) : String{
    return "words: " + wordsLearned.toString() + " | avg per day: " + (wordsLearned.toLong() / ((getNotZeroLong(System.currentTimeMillis() - date)) / 1000 / 60 / 60 / 24)).toString()
}

fun getListNumOf(list : ArrayList<Int>, target: Int) : Int {
    var result = 0
    for (elem in list)
        if (elem == target)
            result += 1
    return result
}

class dicPage {
    private var dicMain : List<String> = ArrayList<String>()
    private var dicTranslation : List<String> = ArrayList<String>()
    private var dicButtonsStates : ArrayList<Int> = ArrayList<Int>()
    private var fileButtonsStates : File = File("")
    private var linesAdded : Int = 0
    private var linesPerAddition : Int = 30
    var colorBackDefault = Color.parseColor("#ffde03")
    var colorBackPressed = Color.parseColor("#0336ff")
    var colorTextDefault = Color.parseColor("#000000")
    var colorTextPressed = Color.parseColor("#ffffff")


    fun setDictionaries(dic1 : List<String>, dic2 : List<String>, buttonsStates : ArrayList<Int>) : Boolean {
        dicMain = dic1
        dicTranslation = dic2
        dicButtonsStates = buttonsStates
        return dicMain.size == dicTranslation.size && dicMain.size == dicButtonsStates.size
    }

    fun setStatesFile(file : File) {
        fileButtonsStates = file
    }

    fun setButtonsColor(defaultBack: Int, pressedBack: Int, defaultText: Int, pressedText: Int) {
        colorBackDefault = defaultBack
        colorBackPressed = pressedBack
        colorTextDefault = defaultText
        colorTextPressed = pressedText
    }

    private fun initButton(button: Button, text: String, colorBack: Int, colorText: Int) {
        button.setText(text)
        button.setBackgroundColor(colorBack)
        button.setTextColor(colorText)
    }

    fun addWords(scrollBox: LinearLayout, context: MainActivity) {
        val linesAddedTotal = linesAdded + linesPerAddition

        while (linesAdded != linesAddedTotal) {

            val buttonIndex = linesAdded
            val newButton = Button(context)
            scrollBox.addView(newButton)

            val textDefault: String = dicMain[buttonIndex] + "\t-\t" + dicTranslation[buttonIndex]
            val textPressed: String = dicMain[buttonIndex]

            if (dicButtonsStates[buttonIndex] == 1) {
                initButton(newButton, textDefault, colorBackPressed, colorTextPressed);
            } else {
                initButton(newButton, textPressed, colorBackDefault, colorTextDefault);
            }

            newButton.setOnClickListener() {
                if (dicButtonsStates[buttonIndex] == 1) {
                    initButton(newButton, textPressed, colorBackDefault, colorTextDefault);
                    dicButtonsStates[buttonIndex] = 0
                } else {
                    initButton(newButton, textDefault, colorBackPressed, colorTextPressed);
                    dicButtonsStates[buttonIndex] = 1
                }

                fileButtonsStates.delete()
                fileButtonsStates.createNewFile()
                val stringButtonsData = StringBuilder()
                for (state in dicButtonsStates) {
                    stringButtonsData.append(state.toString() + "\n".toString())
                }
                fileButtonsStates.writeBytes(stringButtonsData.toString().toByteArray())
                //buttonStat.setText(getStateString(getListNumOf(dicButtonsStates, 1), getNotZeroLong(dateStarted)))
            }

            linesAdded += 1
        }
    }
}

class MainActivity : AppCompatActivity() /*, com.example.lenera.OnScrollChangeListener*/ {
    /*fun addButtons(currIndex: Int, scrollBox: LinearLayout, dicFirst : List<String>, dicSecond: List<String>, buttonsState: ArrayList<Int>) {
        for (index in dicFirst.indices) {

            val button = Button(this)
            scrollBox.addView(button)
            button.setTag(Integer.toString(scrollBox.indexOfChild(button)))
            button.height

            var buttonState = buttonsState[index] == 1

            if (buttonState) {
                button.setBackgroundColor(colorBackPressed)
                button.setTextColor(colorTextPressed)
                button.setText(dicFirst[index] + "\t-\t" + dicSecond[index])
            }
            else {
                button.setBackgroundColor(colorBackDefault)
                button.setTextColor(colorTextDefault)
                button.setText(dicFirst[index])
            }




            button.setOnClickListener() {
                buttonState = buttonsState[index] == 1
                if (buttonState) {
                    button.setBackgroundColor(colorBackDefault)
                    button.setTextColor(colorTextDefault)
                    button.setText(dicFirst[index])
                    buttonsState[index] = 0
                }
                else {
                    button.setBackgroundColor(colorBackPressed)
                    button.setTextColor(colorTextPressed)
                    button.setText(dicFirst[index] + "\t-\t" + dicSecond[index])
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
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide action bar
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_main)


        // INITIALIZATION
        val scrollBox = findViewById<LinearLayout>(R.id.ScrollBox)
        val buttonStat = findViewById<Button>(R.id.buttonStat)

        // get dictionaries from files
        val dicEn = (BufferedReader(assets.open("Dictionaries/Dic1/en.txt").reader()).readText()).lines()
        val dicRu = (BufferedReader(assets.open("Dictionaries/Dic1/ru.txt").reader()).readText()).lines()

        val fileDate = File(applicationContext.filesDir,"fileDate")
        if (!fileDate.exists())
            fileDate.createNewFile()

        //get buttons pressed states from file or create new if doesn't exists
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

        // create dictionary page
        var dictPage = dicPage()
        dictPage.setDictionaries(dicEn, dicRu, buttonsState)
        dictPage.addWords(scrollBox, this)
        dictPage.setStatesFile(fileButtonsData)
        //dictPage.setStatButtonAndDate(buttonStat, getNotZeroLong((fileDate.lastModified()) / 1000 / 60 / 60 / 24))

        //set stat button color
        buttonStat.setBackgroundColor(dictPage.colorBackPressed)
        buttonStat.setTextColor(dictPage.colorTextPressed)
        buttonStat.setText(getStateString(getListNumOf(buttonsState, 1), getNotZeroLong((fileDate.lastModified()))))

        var scrollObject = findViewById<ScrollView>(R.id.BaseScrollView)


        // scroll listen and add words
        scrollObject.viewTreeObserver.addOnScrollChangedListener {
            buttonStat.setText(getStateString(getListNumOf(buttonsState, 1), getNotZeroLong((fileDate.lastModified()))))
            var pageHeight = scrollObject.height
            var allElementsHeight = scrollObject.getChildAt(0).bottom
            var currentPosY = scrollObject.scrollY
            //var bottom = scrollObject.getChildAt(scrollObject.getChildCount() - 1).getHeight() - scrollObject.getHeight() - scrollObject.getScrollY()

            //Log.d("TAGG", "Pos: " + currentPosY.toString() + ", Bottom: " + allElementsHeight.toString() + ", Height: " + pageHeight)
            //Log.d("TAGG", "Pos: " + (currentPosY).toString() + ", Bottom: " + (allElementsHeight - pageHeight).toString() + ", Height: " + pageHeight)
            if ((currentPosY + pageHeight) > (allElementsHeight - pageHeight))
            {
                dictPage.addWords(scrollBox, this)
            }
        }

    }
}