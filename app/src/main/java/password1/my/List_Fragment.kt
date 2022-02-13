package password.my


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.layout_dialog_add_web.view.*
import kotlinx.android.synthetic.main.layout_dialog_check_user_login.view.*
import kotlinx.android.synthetic.main.layout_dialog_delete_web.view.*
import kotlinx.android.synthetic.main.layout_dialog_rename_web.view.*
import kotlinx.coroutines.runBlocking
import password1.my.MainActivity
import password1.my.R
import password1.my.adapter.IClickDetele
import password1.my.adapter.IClickUpdate
import password1.my.adapter.list_adapter
import password1.my.databinding.FragmentListBinding
import password1.my.interface1.IReplaceFrag
import password1.my.interface1.IShowInfoUser
import password1.my.interface1.IToast
import password1.my.model.Web
import password1.my.viewmodel.WebViewModel


class List_Fragment : Fragment(R.layout.fragment_list), IClickUpdate, IClickDetele, IShowInfoUser,
    IToast, IReplaceFrag {


    private lateinit var mainActivity: MainActivity
    private lateinit var navView: NavigationView
    private lateinit var adapter: list_adapter
    private lateinit var mWebViewModel: WebViewModel
    private lateinit var listt: MutableList<Web>
    private lateinit var rcv: RecyclerView
    private var userStorePhone = false
    private val database: DatabaseReference = Firebase.database.reference
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val view = inflater.inflate(R.layout.fragment_list, container, false)
        mainActivity = activity as MainActivity
        mWebViewModel = ViewModelProvider(this).get(WebViewModel::class.java)
        _binding = FragmentListBinding.inflate(inflater, container, false)
        navView = mainActivity.findViewById(R.id.navigation_view)
        rcv = binding.rcvWeb

        val user = Firebase.auth.currentUser

        ShowInfoUser(navView, mainActivity)

        adapter = list_adapter(mainActivity, this, this)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mainActivity)
        rcv.layoutManager = layoutManager


        binding.btnAdd.setOnClickListener {
            if (userStorePhone) {
                DialogAddItemWeb("phone")
                return@setOnClickListener
            }
            if (user != null) {
                DialogAddItemWeb("sever")
            } else {
                DialogCheckUserLogin()
            }
        }


        listt = mutableListOf()
        rcv.adapter = adapter
        if (user != null) {
            val listPhone = mWebViewModel.readListData()
            binding.shimmerRcv.visibility = View.VISIBLE
            if (listPhone.size > 0) {
                val ref = SetRef()
                AddWeb_FromPhone_ToFirebase(ref, listPhone)
                SetUpListFromFireBase(ref)
                MoveShimmer()
            } else {
                val ref = SetRef()
                SetUpListFromFireBase(ref)
                MoveShimmer()
            }

        } else {
            binding.shimmerRcv.stopShimmer()
            binding.shimmerRcv.hideShimmer()
            binding.shimmerRcv.visibility = View.GONE
            listt = mWebViewModel.readListData()
            rcv.visibility = View.VISIBLE
            adapter.setData(listt)
        }

        rcv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    binding.btnAdd.visibility = View.GONE
                }
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == 0) {
                    binding.btnAdd.visibility = View.VISIBLE
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })



        return binding.root
    }

    fun MoveShimmer() {
        binding.shimmerRcv.stopShimmer()
        binding.shimmerRcv.hideShimmer()
        binding.shimmerRcv.visibility = View.GONE
        rcv.visibility = View.VISIBLE
    }

    private fun AddWeb_FromPhone_ToFirebase(ref: String, list: MutableList<Web>) = runBlocking {
        val myref = database.child(ref)
        var id = 1
        val q = database.child(ref).orderByKey().limitToLast(1)
        q.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val a = snapshot.children
                a.forEach {
                    val a2 = it.child("id").getValue().toString()
                    val parsedInt = a2.toInt()
                    id += parsedInt
                }
                for (i in 0 until list.size) {
                    val name = list[i].name
                    val web = Web(id + i, name)
                    myref.child(web.id.toString()).setValue(web)
                    mWebViewModel.deleteWeb(list[i])
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return@runBlocking
    }


    private fun SetRef(): String {
        val user = Firebase.auth.currentUser
        var ref = ""
        val s = user?.email.toString()
        val check = mutableListOf('.', '#', '$', '[', ']')
        for (i in 0 until s.length) {
            if (s[i] == '@') break
            if (!check.contains(s[i])) {
                ref += s[i]
            }
        }
        return ref
    }


    private fun StoreInPhone(name: String) {
        var id = 0
        if (listt.size > 0) {
            id += listt[listt.size - 1].id + 1
        }
        if (!mWebViewModel.checkItemExsit(name)) {
            val web = Web(id, name)
            mWebViewModel.addWeb(web)
            listt.add(web)
            adapter.setData(listt)
        } else {
            Show(getString(R.string.web_already_have), mainActivity)
        }
    }

    private fun StoreInSever(name: String) {
        val ref = SetRef()
        AddWebToFirebase(ref, name)
    }

    private fun AddWebToFirebase(ref: String, name: String) {
        val myref = database.child(ref)
        var id = 1
        val q = database.child(ref).orderByKey().limitToLast(1)
        q.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val a = snapshot.children
                a.forEach {
                    val a2 = it.child("id").getValue().toString()
                    Log.d("tas", "Vao add web trong for " + a2)
                    val parsedInt = a2.toInt()
                    id += parsedInt
                }
                val w2 = Web(id, name)
                myref.child(w2.id.toString()).setValue(w2)
                Log.d("tas", "Ra ngoai for trong add web " + name)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun UpdateWebFirebase(ref: String, name: String, web: Web) {
        val myref = database.child(ref)
        myref.child(web.id.toString()).child("name").setValue(name)
    }

    private fun SetUpListFromFireBase(ref: String) {
        val myref = database.child(ref)
        myref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val name = dataSnapshot.child("name").value.toString()
                val id = dataSnapshot.child("id").value.toString()
                val parseid = id.toInt()

                listt.add(Web(parseid, name))
                adapter.setData(listt)
                rcv.scrollToPosition(listt.size - 1)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val name = dataSnapshot.child("name").value.toString()
                val id = dataSnapshot.child("id").value.toString()
                for (i in 0 until listt.size) {
                    if (listt[i].id == id.toInt()) {
                        listt[i].name = name
                        adapter.notifyItemChanged(i)
                        return
                    }
                }
                Show(getString(R.string.data_go_wrong), mainActivity)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }


    private fun DialogAddItemWeb(check: String) {
        val view = View.inflate(mainActivity, R.layout.layout_dialog_add_web, null)
        val builder = AlertDialog.Builder(mainActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (check == "sever") {
            view.dialog_addweb_tv_title.text = getString(R.string.store_in_sever)
        } else {
            view.dialog_addweb_tv_title.text = getString(R.string.store_in_phone)
        }


        view.dialog_addweb_btn_add_web.setOnClickListener {
            val name = view.dialog_addweb_et_user_web.text.toString()
            Hide_SoftKeyBoard()
            if (name.equals("")) {
                Show(getString(R.string.web_empty), mainActivity)
                return@setOnClickListener
            }
            dialog.dismiss()
            if (check == "sever") {
                StoreInSever(name)
            } else {
                StoreInPhone(name)
            }
        }

        view.dialog_addweb_btn_cancel.setOnClickListener {
            Hide_SoftKeyBoard()
            dialog.dismiss()
        }
    }

    private fun DialogCheckUserLogin() {
        val view = View.inflate(mainActivity, R.layout.layout_dialog_check_user_login, null)
        val builder = AlertDialog.Builder(mainActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.dialog_checkUser_btn_cancel.setOnClickListener {
            userStorePhone = true
            dialog.dismiss()
        }

        view.dialog_checkUser_btn_SignIn.setOnClickListener {
            dialog.dismiss()
            val fm = mainActivity.supportFragmentManager
            replaceFrag(Sign_in_Fragment(), "Frag_sign_up", fm)
        }
    }

    private fun DialogRenameWeb(web: Web, check: String) {
        val view = View.inflate(mainActivity, R.layout.layout_dialog_rename_web, null)
        val builder = AlertDialog.Builder(mainActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (check == "sever") {
            view.dialog_renameWeb_tv_title.text = getString(R.string.rename_in_sever)
        } else {
            view.dialog_renameWeb_tv_title.text = getString(R.string.rename_in_phone)
        }

        view.dialog_renameWeb_btn_rename_web.setOnClickListener {
            val name = view.dialog_renameWeb_et_user_web.text.toString()
            Hide_SoftKeyBoard()
            if (name.equals("")) {
                Show(getString(R.string.web_empty), mainActivity)
                return@setOnClickListener
            }
            if (check == "sever") {
                val ref = SetRef()
                UpdateWebFirebase(ref, name, web)
            } else {
                if (mWebViewModel.checkItemExsit(name)) {
                    Show(getString(R.string.web_already_have), mainActivity)
                } else {
                    web.name = name
                    mWebViewModel.updateWeb(web)
                    adapter.notifyDataSetChanged()
                }
            }
            dialog.dismiss()
        }

        view.dialog_renameWeb_btn_cancel.setOnClickListener {
            Hide_SoftKeyBoard()
            dialog.dismiss()
        }

    }

    private fun DialogDeleteWeb(web: Web, check: String) {
        val view = View.inflate(mainActivity, R.layout.layout_dialog_delete_web, null)
        val builder = AlertDialog.Builder(mainActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (check == "sever") {
            view.dialog_deleteWeb_tv_titlie.text = getString(R.string.delete_in_sever)
        } else {
            view.dialog_deleteWeb_tv_titlie.text = getString(R.string.delete_in_phone)
        }


        view.dialog_deleteWeb_btn_delete.setOnClickListener {
            if (check == "sever") {
                val ref = SetRef()
                val myref = database.child(ref)
                myref.child(web.id.toString())
                    .removeValue(object : DatabaseReference.CompletionListener {
                        override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                            Show(getString(R.string.delete_success), mainActivity)
                        }
                    })
            } else {
                mWebViewModel.deleteWeb(web)
            }
            listt.remove(web)
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        view.dialog_deleteWeb_btn_cancel.setOnClickListener {
            dialog.dismiss()
        }
    }


    override fun onClickUpdate(web: Web) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            DialogRenameWeb(web, "sever")
        } else {
            DialogRenameWeb(web, "phone")
        }

    }

    override fun onClickDelete(web: Web) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            DialogDeleteWeb(web, "sever")
        } else {
            DialogDeleteWeb(web, "phone")
        }
    }

    private fun Hide_SoftKeyBoard() {
        val imm = mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
    }
}


