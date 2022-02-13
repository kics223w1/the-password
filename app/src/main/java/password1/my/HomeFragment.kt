package password.my


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import password1.my.Instruction_Fragment
import password1.my.MainActivity
import password1.my.R
import password1.my.databinding.FragmentHomeBinding
import password1.my.interface1.IReplaceFrag
import password1.my.interface1.IShowInfoUser
import password1.my.interface1.IToast


class HomeFragment : Fragment(R.layout.fragment_home) , IShowInfoUser , IToast , IReplaceFrag{


    private lateinit var mainActivity: MainActivity
    private lateinit var navView: NavigationView
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        navView = mainActivity.findViewById(R.id.navigation_view)

        ShowInfoUser(navView, mainActivity)

        binding.layoutInputKeyCode.setEndIconOnClickListener {
            binding.tvInputKeyCode.setText(R.string.clear)
            binding.showPassword.text = ""
            binding.showAccount.text = ""

        }

        binding.btnInstruction.setOnClickListener {
            mainActivity.mCurrentFragment = -1
            replaceFrag(Instruction_Fragment() ,
                "frag_instruction" , mainActivity.supportFragmentManager)
        }

        binding.btnGeneratePassword.setOnClickListener {
            Hide_SoftKeyBoard()
            val keycode = binding.tvInputKeyCode.text.toString()
            if (keycode == ""){
                Show(getString(R.string.check_keyCode1) , mainActivity)
                return@setOnClickListener
            }
            if(keycode.length > 40){
                Show(getString(R.string.check_keyCode2) , mainActivity)
                return@setOnClickListener
            }
            var keycode2 = ""
            for(i in 0  until keycode.length){
                val low = keycode[i] - 'a'
                val up = keycode[i] - 'A'
                val num = keycode[i] - '0'
                if ((low >= 0 && low < 26) || (up >= 0 && up < 26) || (num >= 0 && num < 26)){
                    keycode2 += keycode[i]
                }
            }
            if (keycode2 == ""){
                Show(getString(R.string.check_keyCode4) , mainActivity)
                return@setOnClickListener
            }
            var ans = ""
            try {
                ans = generatePassword(keycode2)
            }catch (e : Exception){
                Show(getString(R.string.check_keyCode5) , mainActivity)
            }
            var ans2 = ""
            for(i in 0 until ans.length){
                if(ans[i] == 'l'){
                    ans2 += 'L'
                    continue
                }
                ans2 += ans[i]
            }
            binding.showPassword.text = ans2
        }

        binding.btnGenerateAccount.setOnClickListener {
            Hide_SoftKeyBoard()
            val keycode = binding.tvInputKeyCode.text.toString()
            if (keycode == ""){
                Show(getString(R.string.check_keyCode1) , mainActivity)
                return@setOnClickListener
            }
            if(keycode.length > 40){
                Show(getString(R.string.check_keyCode2) , mainActivity)
                return@setOnClickListener
            }
            var keycode2 = ""
            for(i in 0  until keycode.length){
                val low = keycode[i] - 'a'
                val up = keycode[i] - 'A'
                val num = keycode[i] - '0'
                if ((low >= 0 && low < 26) || (up >= 0 && up < 26) || (num >= 0 && num < 26)){
                    keycode2 += keycode[i]
                }
            }
            if (keycode2 == ""){
                Show(getString(R.string.check_keyCode4)  , mainActivity)
                return@setOnClickListener
            }
            var ans = ""
            try {
                ans = generateAccount(keycode2)
            }catch (e : Exception){
                Show(getString(R.string.check_keyCode5) , mainActivity)
            }
            var ans2 = ""
            for(i in 0 until ans.length){
                if(ans[i] == 'l'){
                    ans2 += 'L'
                    continue
                }
                ans2 += ans[i]
            }
            binding.showAccount.text = ans2
        }

        binding.btnCopyPassword.setOnClickListener{
            val pass = binding.showPassword.text.toString()
            if (pass == ""){
                Show("Not thing to copy" , mainActivity)
                return@setOnClickListener
            }
            mainActivity.copyToClipboard(pass)
        }

        binding.btnCopyAccount.setOnClickListener {
            val account = binding.showAccount.text.toString()
            if (account == ""){
                Show("Not thing to copy" , mainActivity)
                return@setOnClickListener
            }
            mainActivity.copyToClipboard(account)
        }

        binding.btnCopyAll.setOnClickListener {
            if(binding.showAccount.text.toString() == ""){
                Show("Account is empty" , mainActivity)
                return@setOnClickListener
            }
            if(binding.showPassword.text.toString() == ""){
                Show("Password is empty" , mainActivity)
                return@setOnClickListener
            }
            val s = binding.showAccount.text.toString() + "\n" + binding.showPassword.text.toString()
            Log.d("tas" , s)
            mainActivity.copyToClipboard(s)
        }

        return binding.root
    }

    fun Context.copyToClipboard(text: CharSequence){
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label",text)
        clipboard.setPrimaryClip(clip)
        Show(getString(R.string.copy_success) , this)
    }


    private fun generatePassword(input : String) : String  {
        var password = ""
        var length_of_password = 0
        if(input.length <= 6) length_of_password = 9 // Ex: @1@H2uyHi
        if(input.length > 6 && input.length <= 14) length_of_password = 10 // Ex: 12Hu@yHu@y
        if(input.length > 14 && input.length <= 27) length_of_password = 12 // Ex: @12Hu@3yh4uy
        if(input.length > 27 && input.length <= 35) length_of_password = 14 // Ex : 1H@uy2Huyh@uyy
        if(input.length > 35 && input.length <= 40) length_of_password = 16 // Ex : @1234HH@5678uy

        //Set character
        var ascII = input.toByteArray()
        for (i in 0 until ascII.size){
            if(password.length >= length_of_password) break
            var index = ascII[i].toInt()
            index %= 40
            password += setChar(input[i] , index)
        }
        //println("password khi moi set ascii: $password")
        ascII = password.toByteArray()
        var pair = 1
        while(password.length < length_of_password){
            if(pair % 2 == 1){
                for(i in ascII.size - 1 downTo 0){
                    if(password.length >= length_of_password) break
                    var index = ascII[i].toInt()
                    index %= 40
                    password += setChar(password[i] , index)
                }
            }else{
                for (i in 0 until ascII.size){
                    if(password.length >= length_of_password) break
                    var index = ascII[i].toInt()
                    index %= 40
                    password += setChar(password[i] , index)
                }
            }
            pair++
        }

        password = password.substring(0 , length_of_password)
        ascII = password.toByteArray()
        val chars = password.toCharArray()
        fun SetSpecificChar(index1 : Int , index2 : Int ){
            val list = listOf('$','@' , '#' , '!' ,'?')
            val id1 = ascII[index1].toInt()
            val id2 = ascII[index2].toInt()
            chars[index1] = list[id1 % list.size]
            chars[index2] = list[id2 % list.size]

        }

        fun SetUpperCase(index1 : Int , index2 : Int ){
            val char = password[index1].uppercaseChar()
            chars[index1] = char
            if(index2 != -1){
                val char2 = password[index2].uppercaseChar()
                chars[index2] = char2
            }

        }

        fun SetNumber(list: MutableList<Int> ) {
            for(i in list){
                var check = ascII[i].toInt()
                check %= 10
                var char = '0' 
                char += check
                chars[i] = char
            }
        }

        when(length_of_password){
            9-> {
                // Ex: @1@H2uyHi
                val list = mutableListOf(1 , 4)
                SetSpecificChar(0 , 2 )
                SetUpperCase(3 , 7 )
                SetNumber(list)
            }
            10-> {
                // Ex: 12Hu@yHu@y
                val list = mutableListOf(0 , 1)
                SetSpecificChar(4 , 7 )
                SetUpperCase(2 , 6)
                SetNumber(list)
            }
            12-> {
                //Ex: @12Hu@3yh4uy
                val list = mutableListOf(1 , 2 , 6  , 9)
                SetSpecificChar(0 , 5)
                SetUpperCase(3 , -1)
                SetNumber(list)
            }
            14-> {
                //Ex: 1H@uy2Huyh@uyy
                val list = mutableListOf(0 , 5)
                SetSpecificChar(2 , 10)
                SetUpperCase(1 , 6)
                SetNumber(list)
            }
            16-> {
                //Ex: @1234HH@5678uy
                val list = mutableListOf(1 , 2 , 3 , 4 , 10 , 11 , 12 , 13)
                SetSpecificChar(0 , 7)
                SetUpperCase(5 , 6)
                SetNumber(list)
            }
        }
        var pass = ""
        for(i in 0 until chars.size){
            pass += chars[i]
        }
        return pass
    }


    private fun generateAccount(input : String) : String  {
        var account = ""
        var length_of_account = 0
        if(input.length <= 12) length_of_account = 8
        if(input.length > 12 && input.length <= 30) length_of_account = 11
        if(input.length > 30) length_of_account = 14

        //Set character
        var ascII = input.toByteArray()
        for (i in 0 until ascII.size){
            if(account.length >= length_of_account) break
            var index = ascII[i].toInt()
            index %= 38
            account += setChar(input[i] , index + 2)
        }
        //println("password khi moi set ascii: $password")
        ascII = account.toByteArray()
        var pair = 1
        while(account.length < length_of_account){
            if(pair % 2 == 1){
                for(i in ascII.size - 1 downTo 0){
                    if(account.length >= length_of_account) break
                    var index = ascII[i].toInt()
                    index %= 38
                    account += setChar(account[i] , index)
                }
            }else{
                for (i in 0 until ascII.size){
                    if(account.length >= length_of_account) break
                    var index = ascII[i].toInt()
                    index %= 38
                    account += setChar(account[i] , index)
                }
            }
            pair++
        }

        account = account.substring(0 , length_of_account)

        ascII = account.toByteArray()
        val chars = account.toCharArray()

        fun SetUpperCase(index1 : Int , index2 : Int ){
            val char = account[index1].uppercaseChar()
            chars[index1] = char
            if(index2 != -1){
                val char2 = account[index2].uppercaseChar()
                chars[index2] = char2
            }

        }

        fun SetNumber(list: MutableList<Int> ) {
            for(i in list){
                var check = ascII[i].toInt()
                check %= 10
                var char = '0'
                char += check
                chars[i] = char
            }
        }

        when(length_of_account){
            8-> {
                // Ex: Huy1Huy2
                val list = mutableListOf(3 , 7)
                SetUpperCase(0 , 4 )
                SetNumber(list)
            }
            11-> {
                // Ex: HuHuy1234y
                val list = mutableListOf(6 , 7 , 8 , 9)
                SetUpperCase(0 , 2)
                SetNumber(list)
            }
            14-> {
                //Ex: Huy123456Huy
                val list = mutableListOf(4 , 5 , 6 , 7 , 8 , 9 )
                SetUpperCase(0 , 11)
                SetNumber(list)
            }
        }
        var Account = ""
        for(i in 0 until chars.size){
            Account += chars[i]
        }
        return Account
    }


    private fun setChar(char : Char , index : Int) : String{
        when(char){
            'a' -> return a[index]
            'b' -> return b[index]
            'c' -> return c[index]
            'd' -> return d[index]
            'e' -> return e[index]
            'f' -> return f[index]
            'g' -> return g[index]
            'h' -> return h[index]
            'i' -> return i[index]
            'j' -> return j[index]
            'k' -> return k[index]
            'l' -> return l[index]
            'm' -> return m[index]
            'n' -> return n[index]
            'o' -> return o[index]
            'p' -> return p[index]
            'q' -> return q[index]
            'r' -> return r[index]
            's' -> return s[index]
            't' -> return t[index]
            'u' -> return u[index]
            'v' -> return v[index]
            'w' -> return w[index]
            'x' -> return x[index]
            'y' -> return y[index]
            'z' -> return z[index]
            'A' -> return A[index]
            'B' -> return B[index]
            'C' -> return C[index]
            'D' -> return D[index]
            'E' -> return E[index]
            'F' -> return F[index]
            'G' -> return G[index]
            'H' -> return H[index]
            'I' -> return I[index]
            'J' -> return J[index]
            'K' -> return K[index]
            'L' -> return L[index]
            'M' -> return M[index]
            'N' -> return N[index]
            'O' -> return O[index]
            'P' -> return P[index]
            'Q' -> return Q[index]
            'R' -> return RR[index]
            'S' -> return S[index]
            'T' -> return T[index]
            'U' -> return U[index]
            'V' -> return V[index]
            'W' -> return W[index]
            'X' -> return X[index]
            'Y' -> return Y[index]
            'Z' -> return Z[index]
            '0' -> return zero[index]
            '1' -> return one[index]
            '2' -> return two[index]
            '3' -> return three[index]
            '4' -> return four[index]
            '5' -> return five[index]
            '6' -> return six[index]
            '7' -> return seven[index]
            '8' -> return eight[index]
            '9' -> return nine[index]
        }
        return ""
    }

    private fun Hide_SoftKeyBoard(){
        val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
    }

    private val a = mutableListOf("rp", "bfcu", "atqe", "cgh", "fe", "dy", "i", "f", "vq", "itfs",
        "jc", "ubiu", "eu", "d", "jnct", "dv", "cb", "y", "sx", "hhlc", "sva", "jsh", "htul", "jgtg", "vw",
        "tu", "rwq", "wk", "bxqt", "rcfg", "oyy", "lb", "tti", "fp", "g", "obv", "btmk", "esmf", "wr", "wv", "@#" , "@")

    private val b = mutableListOf("t", "yi", "v", "tfyi", "zvfh", "rvm", "pg", "pij", "qmo", "qse",
        "j", "mbk", "y", "bf", "peew", "bu", "aa", "ta", "f", "e", "zaus", "c", "pz", "quxw", "vl",
        "k", "l", "nhp", "ycp", "cmvi", "xs", "ijq", "ayo", "rfz", "kwuu", "spu", "minc", "xq", "mory", "jkl", "@@" , "$")

    private val c =  mutableListOf("mi", "gjbl", "zlu", "iow", "qst", "np", "hpd", "noxu", "vcnv", "yxe",
        "otpu", "kns", "s", "brtf", "ubl", "bsx", "ogfh", "yxj", "p", "gi", "b", "vfde", "djc", "lal", "ubt",
        "qeiv", "o", "xieu", "pfl", "en", "i", "oypa", "elzq", "twb", "by", "qaz", "bw", "dts", "j", "ft", "1$" , "2@")

    private val d =  mutableListOf("io", "d", "dlzp", "ov", "o", "lto", "ux", "bwn", "nevl", "j",
        "dp", "x", "cu", "djmg", "ebac", "cv", "yw", "ynfk", "nvvb", "xlt", "tkn", "jbmv", "amio", "c", "z",
        "wkrv", "suj", "jjp", "b", "l", "o", "nggt", "mcw", "wn", "f", "seno", "dng", "sz", "zs", "s", "$$" , "##")

    private val e = mutableListOf("gl", "a", "kvby", "ps", "zljz", "pd", "xs", "cdp", "llq", "fuxb",
        "eql", "omh", "ruq", "jbc", "lig", "ygt", "snz", "bo", "jz", "r", "jjnl", "yime", "rlp", "kbtt", "jhm",
        "kxd", "crum", "c", "jh", "wc", "kzq", "lyp", "xp", "f", "e", "zff", "c", "wuq", "lu", "ta", "$@" , "#@")

    private val f = mutableListOf("cgq", "xh", "neu", "j", "hzk", "iof", "ajbv", "spm", "fu", "kq",
        "kvbd", "bcy", "dhfn", "ydst", "itrz", "av", "f", "jolf", "e", "mxym", "onn", "jc", "ix", "m", "kx",
        "oc", "u", "ccp", "va", "u", "mazt", "iql", "tbo", "zqa", "aakh", "d", "bvnz", "t", "l", "ls", "2#" , "1#")

    private val g = mutableListOf("tohw", "vx", "cepj", "lxd", "ge", "u", "ffvg", "h", "yn", "giex",
        "sysu", "jg", "tpse", "cn", "yro", "gfy", "e", "lyr", "yb", "sd", "js", "v", "ud", "u", "tcz",
        "ipzo", "lp", "dkn", "cwf", "r", "da", "zgz", "hxa", "gyj", "bc", "bv", "d", "c", "qpyb", "z", "4#" , "@2")

    private val h = mutableListOf("rs", "zom", "hnqt", "q", "qx", "xrf", "ihxy", "kdo", "rdkb", "dade",
        "wlf", "ttd", "uu", "ku", "gefe", "f", "llfs", "lxgh", "m", "ocz", "mxdd", "b", "wuux", "flq", "jmnr",
        "n", "xatx", "sm", "fivy", "tmd", "cfjd", "tocg", "bchf", "amij", "cs", "beik", "ehoz", "fkz", "yze", "qrcy", "@1" , "@9")

    private val i = mutableListOf("cne", "damp", "bm", "gc", "tplz", "oqcf", "ncgq", "tcu", "w", "jv",
        "eqy", "db", "jg", "sjrj", "y", "hxxk", "ilvb", "wz", "fmd", "phj", "wzhd", "maz", "y", "hdpc", "gk",
        "bna", "gwrv", "avi", "mawf", "dgh", "hsv", "oshv", "x", "bcb", "ndrn", "k", "nf", "z", "as", "nlx", "@@" , "@@")

    private val j = mutableListOf("avo", "imdn", "ln", "xt", "m", "rkv", "gipj", "hbq", "n", "yk",
        "a", "cwh", "tikm", "tuq", "fu", "kx", "vf", "d", "gy", "izy", "h", "tsh", "r", "yw", "b",
        "duo", "nnw", "bkrr", "qvm", "idwv", "b", "g", "gohf", "h", "uxq", "jsq", "j", "axsg", "jbj", "vzd", "#$" , "$@")

    private val k = mutableListOf("zzjl", "awln", "evdc", "iwxm", "gjy", "rbt", "jf", "zdk", "xqe", "wg",
        "spu", "q", "vt", "cevw", "smz", "w", "p", "a", "tcb", "tsre", "y", "eyko", "gjsv", "vj", "ypfc",
        "age", "x", "c", "sb", "uoe", "bk", "usuy", "h", "uz", "xoeb", "xi", "we", "ao", "ujo", "cty", "$$" , "$$")

    private val l = mutableListOf("qskg", "mtvc", "emv", "lt", "tuas", "qap", "wpg", "wlh", "p", "ok",
        "b", "f", "xv", "ra", "j", "y", "ru", "wpfe", "plg", "kkpd", "etn", "pm", "mpii", "al", "tky",
        "m", "x", "zbh", "q", "j", "dwx", "cc", "uo", "hfhn", "f", "vqmv", "vf", "yh", "goqd", "pl", "@@" , "@1")

    private val m =  mutableListOf("k", "j", "t", "ex", "hrou", "adi", "kmc", "d", "k", "quc",
        "qs", "bdqd", "noro", "oaa", "zjgx", "k", "k", "fzcj", "q", "y", "h", "ey", "ol", "cou", "mgpi",
        "xpz", "hwim", "yb", "kbuw", "qf", "msd", "nfc", "fgvj", "pcp", "zxhj", "ct", "irky", "a", "uzei", "lbu", "@2" , "@8")

    private val n =  mutableListOf("w", "miyr", "ah", "ote", "ey", "dmln", "j", "vut", "wy", "cn",
        "guu", "b", "jw", "uay", "xes", "iid", "g", "p", "zpc", "b", "q", "vb", "art", "dqz", "c",
        "jl", "wrw", "p", "bb", "iy", "y", "pi", "nb", "jiaa", "hjh", "vh", "kxd", "fys", "n", "jihb", "5#" , "6@")

    private val o =  mutableListOf("xpwt", "ei", "nibz", "ofem", "d", "migr", "bz", "wi", "a", "nn",
        "c", "vz", "efpu", "yh", "m", "grdy", "ejd", "bmdx", "juqa", "rfb", "jb", "vug", "dqfe", "v", "m",
        "xef", "dh", "x", "gl", "k", "ftjw", "dl", "rch", "d", "vri", "eai", "l", "md", "ebf", "wu", "@2" , "@1")

    private val p =  mutableListOf("yv", "n", "hskp", "o", "k", "z", "wb", "ntd", "dvo", "wzl",
        "sp", "aaza", "vzu", "mzk", "zejg", "upsp", "dp", "irxj", "h", "khv", "hl", "vs", "vrwn", "s", "h",
        "gfyt", "csm", "h", "uevn", "kvbe", "rre", "efjg", "rcp", "phvu", "pkjb", "wzi", "seo", "v", "uodo", "y", "5#" , "6@")

    private val q =  mutableListOf("syj", "ifft", "x", "hwtb", "niqe", "pcoa", "pcy", "c", "qob", "i",
        "clvv", "wp", "zu", "fuix", "zsb", "r", "e", "g", "xfh", "yfi", "rzfk", "qhj", "hlz", "p", "e",
        "besj", "gguy", "ch", "gy", "khg", "shr", "rwx", "d", "o", "txug", "lznd", "izo", "rzi", "f", "u", "7$" , "9$")

    private val r =  mutableListOf("g", "cj", "zjvk", "b", "od", "ar", "ck", "m", "ig", "tp",
        "vy", "wcr", "ssg", "tqfm", "n", "s", "g", "vap", "gew", "wv", "kl", "tyz", "ax", "nm", "ouj",
        "kjbj", "uxvi", "m", "h", "tas", "fe", "jxf", "j", "quzl", "n", "hzp", "kz", "yua", "bo", "hxss", "$$" , "$@")

    private val s =  mutableListOf("inh", "ypfy", "n", "qphz", "qbkk", "gnt", "djn", "t", "tbj", "luuk",
        "swh", "ifr", "pxng", "xtyg", "kf", "hwf", "qm", "dj", "scp", "ubd", "esc", "nsr", "lwon", "xa", "apqb",
        "scwe", "sr", "sze", "vgo", "ls", "uqe", "hviu", "ew", "m", "aze", "j", "zrba", "x", "ojnd", "faaa", "$2" , "$4")

    private val t =  mutableListOf("a", "g", "g", "kva", "v", "lxz", "ftje", "lg", "k", "m",
        "g", "rjih", "cehm", "bvow", "pf", "ev", "w", "tkm", "cujd", "lw", "bl", "fg", "hwny", "aaw", "mwri",
        "bnv", "ms", "lq", "dji", "gmpv", "hw", "uzih", "hr", "g", "kr", "lc", "tdz", "lasn", "ubwn", "iih", "$8" , "$2")

    private val u =  mutableListOf("n", "l", "pde", "aqs", "wsr", "jnh", "i", "j", "gz", "un",
        "v", "my", "hbel", "pap", "fv", "er", "qmd", "cuhh", "kk", "pls", "tvgo", "h", "vw", "vein", "alw",
        "to", "d", "wn", "fjqk", "yz", "hlvy", "or", "nvo", "o", "c", "ass", "pihv", "wfc", "uvmw", "r", "7@" , "$@")

    private val v =  mutableListOf("xk", "xea", "ypa", "xz", "si", "qrx", "soa", "jdu", "jaq", "zwk",
        "bcdm", "mkm", "w", "xely", "xk", "fhaq", "k", "jcw", "q", "rpyq", "hr", "xm", "omv", "ft", "mis",
        "tsk", "yw", "rkrk", "ywn", "fxvb", "az", "lbof", "zt", "jtot", "a", "o", "z", "xhbx", "btva", "gtxp", "9$" , "$9")

    private val w =  mutableListOf("cz", "jpxl", "ta", "fub", "hd", "tzf", "nen", "dvm", "q", "ta",
        "kvca", "reh", "dbo", "fk", "bu", "xwf", "n", "vixu", "ie", "h", "kv", "w", "upkr", "w", "sg",
        "qum", "qj", "pk", "ca", "ujym", "w", "r", "j", "b", "kyfr", "vc", "trrt", "rbs", "w", "rb", "$2" , "2$")

    private val x =  mutableListOf("gtv", "vi", "bt", "l", "r", "ptw", "hxdr", "ig", "uxri", "ug",
        "jhtu", "aim", "gtc", "u", "ned", "qgf", "bi", "e", "wg", "h", "i", "bh", "nhdx", "wd", "c",
        "spk", "slxc", "xkgp", "edv", "bkgp", "h", "b", "y", "wwq", "zmx", "ae", "kz", "qs", "tfoj", "ysmm", "$$" , "$$")

    private val y = mutableListOf("x", "p", "lo", "eyry", "wvj", "voo", "wid", "v", "fthc", "fdr",
        "fcbf", "f", "o", "q", "y", "qa", "eacg", "y", "k", "guny", "olm", "oyi", "xh", "y", "m",
        "dcq", "uz", "yv", "shk", "ls", "gp", "mkkc", "eu", "cndb", "q", "dde", "yfw", "a", "q", "pkuk", "6$" , "@@")

    private val z = mutableListOf("zjae", "mvnh", "oxa", "r", "wum", "a", "poqa", "iq", "xiao", "yp",
        "myqp", "swsk", "xolo", "nio", "q", "ffwu", "rth", "n", "v", "l", "oqsk", "u", "davq", "ml", "x",
        "hdu", "sms", "rypp", "uo", "yeu", "mxcj", "jx", "n", "mhge", "vbu", "mxxd", "ug", "vms", "nsga", "df","@@" , "$$" )

    private val A = mutableListOf("xprg", "rgde", "ghuv", "yzka", "nvkm", "yqj", "yqdy", "t", "qloz", "v",
        "x", "jfn", "wozs", "xbd", "emh", "p", "ng", "i", "cbxd", "v", "wi", "d", "unc", "axwv", "nkiz",
        "zr", "p", "t", "eg", "z", "u", "a", "v", "zwvk", "kd", "wn", "qpk", "ksbg", "inqs", "o", "4$" , "5@")

    private val B = mutableListOf("y", "ubwi", "jt", "hnxf", "qrw", "l", "rwy", "vj", "bht", "fvgp",
        "tp", "saai", "wqg", "d", "cv", "noy", "de", "e", "wqs", "jhj", "mvbd", "nt", "oo", "j", "szeu",
        "wioa", "ofu", "xtx", "arsa", "lu", "s", "bcy", "yj", "rv", "oabe", "qxcz", "r", "dpht", "oat", "aik", "8$" , "2@")

    private val C = mutableListOf("n", "yq", "eet", "v", "as", "zv", "pb", "vd", "gii", "mw",
        "mok", "r", "ltbf", "d", "d", "y", "hu", "imw", "gt", "ovk", "ak", "r", "nxbd", "zyzl", "yg",
        "yr", "po", "cb", "ba", "bz", "gpj", "w", "cfk", "gse", "zc", "ygk", "k", "kt", "do", "wgdj", "$5" , "7@")

    private val D = mutableListOf("tcwo", "xn", "cao", "ts", "paao", "nuzl", "kxnw", "tgr", "oh", "ce",
        "wo", "j", "ifoe", "gmgh", "bipj", "koo", "ky", "qa", "v", "cz", "l", "ey", "qos", "v", "iy",
        "jdzj", "ajuv", "e", "ag", "ujkg", "usyj", "cjw", "dw", "b", "jpop", "n", "hpe", "heh", "zbno", "fhpf", "@$" , "$@")

    private val E = mutableListOf("ewik", "cokf", "hoc", "rgrf", "h", "cck", "pxdy", "yxdk", "ehya", "dpbr",
        "zo", "lz", "y", "fmm", "jw", "oklk", "z", "m", "qvqd", "xp", "avlo", "ion", "barg", "ghx", "bvr",
        "l", "znsm", "rww", "cyny", "pcz", "gqw", "arw", "atmy", "um", "mcu", "p", "cxep", "ub", "m", "jpp", "9#" , "#@")

    private val F = mutableListOf("sq", "fu", "w", "mes", "nbih", "p", "hazo", "zf", "uq", "on",
        "m", "beop", "bism", "a", "it", "pnk", "mvv", "u", "o", "l", "lf", "ktl", "ts", "zlpu", "rd",
        "wv", "stwn", "hz", "ae", "jjoz", "xny", "ypbs", "xw", "vca", "ck", "o", "xc", "exva", "goh", "bld", "##" , "$$")

    private val G = mutableListOf("b", "mrc", "lau", "t", "xwrz", "k", "vgax", "m", "kwv", "gos",
        "w", "nou", "hqj", "c", "j", "d", "zvv", "exqp", "n", "aia", "jlc", "qelh", "cnen", "qpsq", "jr",
        "w", "zgke", "tcsz", "d", "e", "to", "zk", "i", "a", "w", "j", "fxf", "f", "rax", "wmm", "$$" , "$#")

    private val H = mutableListOf("i", "hat", "tzu", "w", "bj", "uf", "hadu", "hd", "bw", "gwsv",
        "xtpq", "xvk", "bac", "gb", "m", "asny", "k", "wb", "sink", "u", "kksr", "fsoh", "cajw", "vgi", "giyg",
        "ez", "k", "gajk", "q", "bn", "d", "wjl", "bwen", "givx", "z", "lj", "brbk", "w", "e", "whc", "$2" , "$#")

    private val I = mutableListOf("xu", "hysp", "r", "mdpk", "u", "ami", "jv", "z", "gv", "tzks",
        "bfj", "qcxz", "p", "xgt", "l", "cmd", "ej", "lxdl", "k", "ums", "g", "cazo", "oscp", "bjwa", "o",
        "h", "kvw", "by", "id", "n", "esq", "e", "ouhz", "x", "zh", "cojx", "eivv", "rv", "qyy", "ltk", "$2" , "$$")

    private val J = mutableListOf("gvrj", "xb", "nfj", "zrvw", "neij", "qa", "jzue", "kxsu", "cz", "tkvs",
        "uvw", "sua", "vct", "p", "rm", "uym", "duc", "yew", "hpp", "h", "k", "wsa", "r", "hwm", "v",
        "enjq", "ssv", "ew", "qg", "e", "p", "jrzm", "whlz", "g", "jope", "gpih", "w", "wkp", "b", "wrgd", "$$" , "$2")

    private val K = mutableListOf("yumf", "zfxq", "rqnh", "mo", "ddz", "e", "lrb", "ov", "b", "c",
        "gs", "mm", "meau", "pb", "mz", "yx", "lt", "ks", "mpt", "f", "gbg", "av", "u", "x", "gfhj",
        "fit", "f", "jbpu", "vztm", "yum", "f", "qesl", "xmd", "qvfx", "busr", "zvv", "i", "rjg", "fxmi", "ukqc", "1$" , "2@")

    private val L = mutableListOf("tcp", "f", "vi", "sdje", "fb", "gkx", "g", "do", "z", "e",
        "nxd", "vvxg", "m", "l", "angb", "k", "ctk", "siy", "yb", "g", "a", "qcu", "iod", "pdio", "pzx",
        "q", "x", "aqj", "cyto", "o", "rfh", "gzqi", "mbxp", "ys", "rocj", "j", "i", "icx", "vk", "i", "@$" , "$@")

    private val M = mutableListOf("cyak", "wiql", "cdg", "av", "ux", "jvnd", "upo", "gjl", "kql", "jq",
        "ab", "bt", "viva", "zsf", "adjo", "tbid", "p", "r", "xjv", "qnjk", "fstv", "gr", "xpq", "syg", "n",
        "ij", "hm", "wswk", "mnc", "xw", "oi", "q", "jsju", "x", "hiog", "lleh", "psla", "z", "htj", "eojl", "4@" , "##")

    private val N = mutableListOf("lcux", "yu", "xwj", "w", "jrz", "vhi", "jr", "b", "a", "j",
        "v", "tmu", "t", "heh", "ad", "njp", "zo", "ema", "ud", "qii", "dh", "xs", "r", "tanb", "u",
        "ly", "zaz", "wq", "dnb", "ctab", "emnn", "zmll", "gnz", "npcv", "pa", "hj", "lem", "dlhj", "vwob", "ydt", "@@" , "#@")

    private val O = mutableListOf("ugu", "th", "vd", "dypk", "u", "t", "tkl", "voc", "m", "b",
        "oeij", "iwt", "bq", "rgt", "xjc", "fuy", "zn", "zbqx", "gf", "uqzo", "j", "gro", "h", "nhby", "gucr",
        "vtu", "tjq", "low", "gbz", "df", "vyws", "ecj", "t", "wl", "dlpl", "bvqx", "fw", "ua", "fn", "bcdl", "@#" , "#@")

    private val P = mutableListOf("bjn", "vaji", "yn", "j", "wmb", "qfr", "i", "nk", "i", "yzs",
        "djtt", "yddo", "oqkv", "uu", "c", "d", "yfs", "tcp", "f", "vfre", "rkc", "nd", "bnoz", "gt", "uzxz",
        "fo", "p", "e", "ft", "vij", "jj", "sesn", "jh", "mlqr", "kud", "hinn", "gwr", "iyk", "a", "ld", "@2" , "1@")

    private val Q = mutableListOf("nkh", "xh", "klbh", "gzo", "nlr", "hd", "ulfb", "hfxg", "li", "quys",
        "c", "k", "snq", "vw", "bow", "vtch", "eo", "yt", "tm", "o", "p", "lkmm", "sve", "olwn", "y",
        "m", "uj", "mn", "a", "mv", "klr", "hc", "kc", "jzcm", "pv", "oqh", "zo", "ieoc", "df", "br", "$2" , "$8")

    private val RR = mutableListOf("g", "neh", "s", "phnq", "tb", "od", "pxm", "zn", "sos", "hqb",
        "fink", "tyl", "tdb", "wb", "ljv", "b", "nyj", "m", "oqx", "vma", "gt", "nr", "gb", "sw", "pa",
        "u", "t", "uanj", "hrl", "blt", "pamz", "dg", "dam", "m", "er", "pz", "ggjw", "mbjr", "oy", "uhke", "2$" , "1@")

    private val S = mutableListOf("jazu", "qwc", "d", "ovd", "ou", "psun", "m", "x", "b", "w",
        "tgu", "pov", "svr", "qo", "blo", "bpht", "r", "ppaq", "nbhx", "lnxy", "ubg", "vgs", "icck", "uwh", "unxn",
        "zj", "r", "d", "ab", "fs", "dzst", "zv", "ugpu", "jmmy", "dfif", "dm", "nzq", "yd", "ixj", "wbd", "$@" , "$@")

    private val T = mutableListOf("enb", "vhdd", "vt", "ep", "q", "dou", "bi", "pkur", "bkj", "rv",
        "rvh", "wnul", "v", "wcpi", "x", "xbx", "i", "k", "znjc", "upy", "zwj", "cqrp", "s", "gxbu", "fme",
        "p", "u", "e", "ans", "uxyy", "i", "o", "i", "vzec", "ccfz", "odaa", "gmsc", "uz", "qtny", "ssfy", "$$" , "$@")

    private val U = mutableListOf("xt", "dx", "dpbb", "bdu", "wa", "sqtv", "xhkj", "sep", "fmf", "zy",
        "vf", "dg", "dc", "z", "vjy", "e", "p", "e", "itwn", "gtd", "lk", "f", "qkmt", "udp", "uog",
        "pcs", "xic", "hhpi", "jah", "ruz", "xi", "pfnd", "d", "m", "to", "o", "lwkx", "hgoo", "b", "np", "$4" , "$6")

    private val V = mutableListOf("nf", "qj", "zyhb", "xc", "sd", "fi", "p", "rfe", "rw", "qibw",
        "ao", "q", "m", "osgy", "vr", "slu", "y", "um", "xaf", "lrmn", "skr", "vdod", "rnur", "bhzc", "y",
        "an", "m", "fahl", "kej", "ipwy", "eac", "ar", "n", "ukbw", "d", "wq", "pma", "lh", "p", "lxg", "1$" , "1$")

    private val W = mutableListOf("xpc", "qx", "oge", "mi", "rel", "b", "od", "s", "nepe", "tk",
        "pqut", "wiq", "p", "xrfl", "dpvv", "rdrq", "mpty", "lcyl", "gynj", "chp", "kb", "d", "bycs", "qbdx", "uqcg",
        "m", "r", "c", "wlkj", "frs", "epp", "ni", "w", "vtb", "mmzm", "tf", "t", "amgj", "lnc", "ptuy", "$$" , "$2")

    private val X = mutableListOf("zpfx", "q", "y", "z", "sejt", "y", "o", "u", "dvj", "kav",
        "sjux", "dxi", "tjkg", "uoa", "kqaw", "q", "bob", "s", "urog", "yiys", "auh", "fe", "o", "f", "z",
        "z", "gtw", "ffyc", "fw", "f", "on", "dmo", "mrf", "xuzu", "a", "iisl", "c", "sey", "hfcl", "mcm", "$2" , "$@")

    private val Y = mutableListOf("rut", "ens", "pn", "j", "r", "snnt", "np", "hn", "akt", "mc",
        "e", "mpk", "j", "bi", "com", "g", "lriu", "jfn", "jv", "nwr", "jp", "qp", "l", "s", "xa",
        "yla", "bfb", "e", "czci", "obui", "i", "r", "wbm", "a", "qfiy", "j", "noh", "nq", "uc", "agx", "@$" , "$@")

    private val Z = mutableListOf("yaj", "jiel", "uyq", "mb", "w", "c", "poem", "fgm", "fr", "s",
        "jupu", "vkgi", "cdm", "njs", "x", "cy", "l", "use", "ggf", "ft", "qmx", "v", "ykh", "kj", "aapi",
        "g", "rj", "ucl", "x", "ytu", "zq", "dq", "gyb", "li", "wf", "kznv", "mosc", "likz", "a", "bg", "$4" , "1@")

    private val zero = mutableListOf("chff", "y", "now", "wffc", "c", "yc", "yie", "hanj", "qfz", "s",
        "fxro", "yy", "ebp", "n", "hey", "bab", "lk", "elum", "omhq", "m", "pgfc", "nmf", "nh", "eqa", "ufr",
        "bqqt", "rqa", "bzj", "ihch", "srbz", "s", "roa", "c", "ectu", "awdi", "mmad", "i", "amur", "cf", "ltwx", "2$" , "$@")

    private val one = mutableListOf("jgdx", "cx", "ukx", "y", "baex", "wq", "e", "gh", "cwva", "pnzk",
        "vd", "qsn", "n", "xgn", "lh", "yp", "g", "d", "oz", "aasg", "oqq", "jy", "aaw", "ked", "xpd",
        "t", "fn", "k", "j", "pxgc", "hnnh", "d", "xrz", "m", "jy", "wtmb", "zt", "gwll", "oalw", "re", "$$" , "$@")

    private val two = mutableListOf("dro", "p", "n", "qyc", "n", "jidh", "asg", "ilk", "bnb", "x",
        "b", "rneh", "aaqu", "vw", "aglw", "bta", "io", "y", "u", "fzp", "wt", "mkm", "m", "kjnk", "hdg",
        "u", "nli", "c", "teiv", "f", "n", "nb", "gavw", "kvje", "xibr", "ifk", "bo", "eda", "u", "zhol", "6$" , "7@")

    private val three = mutableListOf("mf", "eyno", "wz", "okmr", "cuig", "jazz", "nzt", "ddu", "n", "ake",
        "lza", "mclb", "iwok", "q", "kjf", "j", "oe", "iac", "grzx", "r", "p", "go", "qef", "b", "d",
        "w", "etok", "rj", "hyb", "bi", "tgq", "yzhl", "zvf", "r", "p", "vwl", "sz", "d", "f", "ski", "@@" , "9$")

    private val four = mutableListOf("ypr", "an", "uz", "hcp", "bhmd", "kn", "pdgx", "lj", "nbfm", "mymn",
        "li", "ba", "amji", "duez", "je", "knr", "mnvv", "bq", "w", "el", "ck", "bd", "gnhs", "otf", "tzi",
        "w", "tezr", "ap", "wdv", "mrfj", "a", "xhkb", "jqqv", "lr", "l", "e", "iiq", "fv", "zqat", "b", "$$" , "9@")

    private val five = mutableListOf("m", "v", "vtuk", "lwec", "ti", "jyl", "rhcy", "pjkd", "ppg", "z",
        "c", "yf", "yxne", "jono", "fr", "q", "e", "hv", "o", "pfre", "zgx", "b", "kgvn", "u", "nmrg",
        "xzs", "ih", "ybd", "wiu", "qx", "e", "xhtb", "l", "oeq", "mqbu", "qczs", "c", "yt", "lqg", "hpk", "$$" , "$@")

    private val six = mutableListOf("sj", "coy", "nu", "op", "mrb", "wocx", "frv", "pax", "hc", "npou",
        "x", "fp", "znob", "r", "sgxy", "u", "tlur", "idq", "or", "xeof", "wvxa", "oz", "gwp", "qup", "fy",
        "wdu", "qgdn", "yu", "wd", "rvi", "oii", "hex", "hyis", "maq", "dclg", "ev", "xq", "zbtq", "gfb", "s", "$2" , "$4")

    private val seven = mutableListOf("eibk", "qn", "wwr", "llxw", "t", "qu", "zj", "ywzb", "ex", "bw",
        "hms", "jhg", "tt", "bls", "l", "uh", "qhud", "xxx", "josz", "lzb", "dsp", "pzfh", "vu", "re", "b",
        "swv", "g", "yti", "mis", "u", "nbs", "tt", "fba", "xqr", "en", "m", "x", "qumc", "ux", "fqgx", "$$" , "$$")

    private val eight = mutableListOf("hb", "d", "cwkd", "ml", "dmd", "mb", "oo", "yjz", "p", "pli",
        "kjp", "ty", "upt", "u", "l", "kcdv", "tl", "l", "fmz", "cu", "jvb", "v", "wtr", "aak", "pp",
        "jbc", "xi", "tejr", "uph", "rg", "mmta", "zx", "vtc", "we", "kmp", "vsh", "jv", "ijh", "wbyd", "azsx", "#$" , "$#")

    private val nine = mutableListOf("h", "wjo", "glf", "x", "it", "g", "ep", "qi", "gic", "klq",
        "jap", "fsub", "idu", "rd", "lxc", "sx", "pq", "td", "oki", "f", "ytsc", "ohr", "v", "kti", "jeb",
        "mo", "icaz", "vsge", "zruy", "czaw", "uagc", "ehkj", "epxo", "s", "i", "zaq", "rkps", "b", "m", "kw", "$$" , "$$")


}