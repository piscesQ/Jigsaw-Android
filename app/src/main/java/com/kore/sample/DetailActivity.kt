package com.kore.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kore.jigsaw.anno.router.Autowired
import com.kore.jigsaw.anno.router.Route
import com.kore.jigsaw.core.router.JRouter
import com.kore.sample.bean.Computer
import com.kore.sample.bean.Person
import kotlinx.android.synthetic.main.activity_detail.*
import java.io.File

/**
 * @author koreq
 * @date 2021-04-13
 * @description 用于测试路由跳转
 */
@Route(path = "/detail")
class DetailActivity : AppCompatActivity() {

    @Autowired(name = "type")
    @JvmField
    var intType = 1

    @Autowired(name = "key")
    @JvmField
    var strKey = "温度"

    @Autowired(name = "flag")
    @JvmField
    var boolFlag = false

    @Autowired(name = "price")
    @JvmField
    var doublePrice = 11.11

    @Autowired(name = "letter")
    @JvmField
    var charLetter = 'a'

    @Autowired(name = "music", desc = "Serialiable 对象： File")
    @JvmField
    var fileMusic: File? = File("/User/Downloads/111.mp3")

    @Autowired(name = "user", desc = "用户信息，普通 Object 对象")
    @JvmField
    var objUser: User? = User("阳光", 11)

    @Autowired(name = "computer", desc = "Parcelable 对象 computer")
    @JvmField
    var objComputer: Computer? = Computer("1", "Laptop", "Dell")

    @Autowired(name = "person", desc = "Parcelable 对象 Person")
    @JvmField
    var objPerson: Person? = Person("用户名", "密码")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JRouter.get().inject(this)
        setContentView(R.layout.activity_detail)
        var showText = "intType = $intType, \n" +
                "strKey = $strKey, \n" +
                "boolFlag = $boolFlag,\n" +
                " doublePrice = $doublePrice, \n" +
                "charLetter = $charLetter, \n" +
                "fileMusic = $fileMusic, \n" +
                "objUser = $objUser, \n" +
                "objComputer = $objComputer, \n" +
                "objPerson = $objPerson, \n"
        tv_info.text = showText
    }


    class User(var name: String, var age: Int) {

        override fun toString(): String {
            return "User(name='$name', age=$age)"
        }
    }
}