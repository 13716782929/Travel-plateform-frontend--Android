package iss.nus.edu.sg.mygo.enum

// 弃用
enum class RoomType(val displayName: String){
    REGULAR("标准间"),
    SUPREME("豪华间"),
    SUITE("套房");

    companion object {
        fun fromString(value: String?): RoomType {
            return values().find { it.displayName == value } ?: REGULAR
        }
    }}