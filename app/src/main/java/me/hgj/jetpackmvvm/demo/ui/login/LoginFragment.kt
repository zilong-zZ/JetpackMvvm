package me.hgj.jetpackmvvm.demo.ui.login

import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.include_toolbar.*
import me.hgj.jetpackmvvm.demo.R
import me.hgj.jetpackmvvm.demo.app.base.BaseFragment
import me.hgj.jetpackmvvm.demo.app.ext.hideSoftKeyboard
import me.hgj.jetpackmvvm.demo.app.ext.initClose
import me.hgj.jetpackmvvm.demo.app.ext.showMessage
import me.hgj.jetpackmvvm.demo.app.util.CacheUtil
import me.hgj.jetpackmvvm.demo.app.util.SettingUtil
import me.hgj.jetpackmvvm.demo.databinding.FragmentLoginBinding
import me.hgj.jetpackmvvm.ext.parseState
import me.hgj.jetpackmvvm.ext.util.dp2px
import me.hgj.jetpackmvvm.ext.util.setOnclickNoRepeat

/**
 * 作者　: hegaojian
 * 时间　: 2019/12/23
 * 描述　:
 */
class LoginFragment : BaseFragment<LoginRegisterViewModel, FragmentLoginBinding>() {

    override fun layoutId() = R.layout.fragment_login

    override fun initView() {
        mDatabind.viewmodel = mViewModel

        toolbar.initClose("登录") {
            hideSoftKeyboard(activity)
            Navigation.findNavController(it).navigateUp()
        }
        //设置颜色跟主题颜色一致
        appViewModel.appColor.value?.let {
            SettingUtil.setShapColor(login_sub, it)
            login_goregister.setTextColor(it)
            toolbar.setBackgroundColor(it)
        }
    }

    override fun createObserver() {
        //监听请求结果
        mViewModel.loginResult.observe(viewLifecycleOwner, Observer { viewState ->
            parseState(viewState, {
                //登录成功 通知账户数据发生改变鸟
                CacheUtil.setUser(it)
                appViewModel.userinfo.postValue(it)
                Navigation.findNavController(toolbar).navigateUp()
            }, {
                //登录失败
                showMessage(it.errorMsg)
            })
        })
    }

    override fun lazyLoadData() {

    }

    /**
     * 设置点击事件 ,根据黄油刀的风格来仿写的
     */
    override fun onViewClicked() {
        setOnclickNoRepeat(login_clear, login_sub, login_goregister) {
            when (it.id) {
                R.id.login_clear -> {

                    mViewModel.username.set("")
                }
                R.id.login_sub -> {
                    when {
                        mViewModel.username.get().isEmpty() -> showMessage("请填写账号")
                        mViewModel.password.get().isEmpty() -> showMessage("请填写密码")
                        else -> mViewModel.loginReq()
                    }
                }
                R.id.login_goregister -> {
                    hideSoftKeyboard(activity)
                    Navigation.findNavController(it)
                        .navigate(R.id.action_loginFragment_to_registerFrgment)
                }
            }
        }
        login_key.setOnCheckedChangeListener { buttonView, isChecked ->
            mViewModel.isShowPwd.set(
                isChecked
            )
        }
    }

}