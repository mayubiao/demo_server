package utils

object MLog{
    fun print(msg:String?){
        msg?.let {
            System.out.println(it)
        }

    }
}