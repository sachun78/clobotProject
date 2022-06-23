package com.lge.support.second.application.managers

import android.content.Context
import android.util.Log
import jxl.Sheet
import jxl.Workbook

class ExcelMgr(context: Context, val path: String, val sheet: String = "") {
    private val mStream by lazy { context.resources.assets.open(path) }
    private val mWorkBook by lazy { Workbook.getWorkbook(mStream) }

    fun readData(): MutableList<List<String>> {
        var localSheet: Sheet = if (sheet.isEmpty()) {
            mWorkBook.getSheet(0)
        } else {
            mWorkBook.getSheet(sheet)
        }

        val arrReadData: MutableList<List<String>> = mutableListOf()
        val totalColumn = localSheet.columns
        val totalRow = localSheet.rows

        for (idx in 1 until totalRow) {
            var rowContents = localSheet.getRow(idx)
            if (rowContents.isNotEmpty()) {
                var arrTmp: List<String> = rowContents.map {
                    it.contents.toString()
                }
                arrReadData.add(arrTmp)
            }
        }
        //Log.i("hjbae", "$arrReadData")
        return arrReadData
    }
}