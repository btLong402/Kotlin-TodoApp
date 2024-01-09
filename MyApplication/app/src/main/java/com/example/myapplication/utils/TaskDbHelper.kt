package com.example.myapplication.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.utils.model.ToDoData

class TaskDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "task.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_TASKS_TABLE = "CREATE TABLE " + TaskEntry.TABLE_NAME + " (" +
                TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskEntry.COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                TaskEntry.COLUMN_TASK_DESCRIPTION + " TEXT);"
        db.execSQL(SQL_CREATE_TASKS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun addTask(taskName: String, taskDescription: String) {
        // Get writable database
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(TaskEntry.COLUMN_TASK_NAME, taskName)
            put(TaskEntry.COLUMN_TASK_DESCRIPTION, taskDescription)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(TaskEntry.TABLE_NAME, null, values)

        db.close()
    }
    fun updateTask(taskData: ToDoData) {
        // Get writable database
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(TaskEntry.COLUMN_TASK_NAME, taskData.task)
            put(TaskEntry.COLUMN_TASK_DESCRIPTION, taskData.taskDescription)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.update(TaskEntry.TABLE_NAME, values, "${TaskEntry._ID} = ?", arrayOf(taskData.taskId))

        db.close()
    }
    fun deleteTask(taskData: ToDoData) {
        // Get writable database
        val db = this.writableDatabase

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.delete(TaskEntry.TABLE_NAME, "${TaskEntry._ID} = ?", arrayOf(taskData.taskId))

        db.close()
    }

    fun getTasks(): MutableList<ToDoData> {
        val taskList = mutableListOf<ToDoData>()
        val db = this.readableDatabase
        val cursor = db.query(
            TaskEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        with(cursor) {
            while (moveToNext()) {
                val task = ToDoData(
                    getString(getColumnIndexOrThrow(TaskEntry._ID)),
                    getString(getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_NAME)),
                    getString(getColumnIndexOrThrow(TaskEntry.COLUMN_TASK_DESCRIPTION))
                )
                taskList.add(task)
            }
        }
        return taskList
    }
}