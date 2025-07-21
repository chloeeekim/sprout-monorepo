package chloe.sprout.backend.util

object TestUtils {
    fun <T : Any> setPrivateField(obj: T, fieldName: String, value: Any?) {
        val field = obj::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }

    fun <T : Any> setSuperClassPrivateField(obj: T, fieldName: String, value: Any?) {
        val field = obj::class.java.superclass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.set(obj, value)
    }
}