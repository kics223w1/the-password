package password1.my.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import password1.my.R
import password1.my.model.Web

interface IClickDetele{
    fun onClickDelete(web : Web)
}

interface IClickUpdate{
    fun onClickUpdate(web : Web)
}


class list_adapter(
    val context: Context,
    val mIClickDelete : IClickDetele,
    val mIClickUpdate : IClickUpdate
    ) :
    RecyclerView.Adapter<list_adapter.ViewHolder>() {

    private var list_web = mutableListOf<Web>()
    private var viewBind: ViewBinderHelper = ViewBinderHelper() //Focus this

    fun setData(list: MutableList<Web>) {
        list_web = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): list_adapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_rcv_web, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: list_adapter.ViewHolder, position: Int) {
        val item : Web = list_web[position]
        item.id.toString()
        var textt = item.name
        holder.tv_web_item.text = textt
        viewBind.bind(holder.swipeLayout, item.id.toString())

        viewBind.setOpenOnlyOne(true)

        holder.tv_edit_web_item.setOnClickListener {
            mIClickUpdate.onClickUpdate(item)
            val handler = android.os.Handler()
            val run = Runnable {
                holder.swipeLayout.close(true)
            }
            handler.postDelayed(run , 2000)
        }

        holder.tv_delete_web_item.setOnClickListener {
            mIClickDelete.onClickDelete(item)
            val handler = android.os.Handler()
            val run = Runnable {
                holder.swipeLayout.close(true)
            }
            handler.postDelayed(run , 2000)
        }



    }

    override fun getItemCount(): Int {
        return list_web.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var swipeLayout: SwipeRevealLayout
        var tv_web_item: TextView
        var tv_edit_web_item: TextView
        var tv_delete_web_item: TextView

        init {
            swipeLayout = itemView.findViewById(R.id.layout_swipe) //Focus this
            tv_web_item = itemView.findViewById(R.id.tv_item_web)
            tv_edit_web_item = itemView.findViewById(R.id.tv_edit_web_item)
            tv_delete_web_item = itemView.findViewById(R.id.tv_delete_web_item)
        }
    }


}