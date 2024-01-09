import android.provider.BaseColumns

object TaskEntry : BaseColumns {
    const val _ID = BaseColumns._ID
    const val TABLE_NAME = "tasks"
    const val COLUMN_TASK_NAME = "name"
    const val COLUMN_TASK_DESCRIPTION = "description"
}