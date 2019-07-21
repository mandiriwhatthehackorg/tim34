package com.mandiri.whatthehack.audiovideo.other

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Created by andreyyoshuamanik on 9/15/17.
 */

object Util {

    var defaultFormattedDate = "yyyy-MM-dd HH:mm:ss.SSS"

    fun showProgressDialogWithTitle(title: String, context: Context): ProgressDialog {
        val dialog = ProgressDialog(context)
        dialog.setMessage(title)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        return dialog
    }


    fun formattedDate(dateString: String): String {
        var dateFormat = SimpleDateFormat(Util.defaultFormattedDate)
        try {
            val date = dateFormat.parse(dateString)
            dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
            return dateFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return ""
    }

    fun localToUtcFormatDate(date: Date): String {
        val dateFormat = SimpleDateFormat(Util.defaultFormattedDate, Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }

    fun formattedDate(dateString: String, fromDateFormat: String, toDateFormat: String): String {

        var dateFormat = SimpleDateFormat(fromDateFormat)
        try {
            val date = dateFormat.parse(dateString)
            dateFormat = SimpleDateFormat(toDateFormat, Locale("id", "ID"))
            return dateFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return ""
    }

    fun utcToLocalformattedDate(dateString: String, fromDateFormat: String, toDateFormat: String): String {

        var dateFormat = SimpleDateFormat(fromDateFormat)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val date = dateFormat.parse(dateString)
            dateFormat = SimpleDateFormat(toDateFormat, Locale("id", "ID"))
            dateFormat.timeZone = TimeZone.getDefault()
            return dateFormat.format(date!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return ""
    }

    fun showConfirmationWithTitle(title: String, context: Context, confirmedListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(context).setTitle(title).setPositiveButton("OK", confirmedListener)
            .setNegativeButton("Cancel", null).show()
    }
}
