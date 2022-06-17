package com.anpln.uniboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anpln.uniboard.accounthelper.AccountHelper
import com.anpln.uniboard.act.EditAdsAct
import com.anpln.uniboard.adapters.AdsRcAdapter
import com.anpln.uniboard.databinding.ActivityMainBinding
import com.anpln.uniboard.dialoghelper.DialogConst
import com.anpln.uniboard.dialoghelper.DialogHelper
import com.anpln.uniboard.dialoghelper.GoogleAccConst
import com.anpln.uniboard.model.Ad
import com.anpln.uniboard.viewmodel.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdsRcAdapter.Listener {
    private lateinit var tvAccount: TextView
    private lateinit var rootElement: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth//объект для аутентификации
    val adapter = AdsRcAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
        initRecyclerView()
        //получение прямого доступа к элементам экрана
        initViewModel()
        //отслеживание изменений view
        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()

    }

    override fun onResume() {
        super.onResume()
        rootElement.mainContent.bNavView.selectedItemId = R.id.id_home
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return super.onOptionsItemSelected(item)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE) {
            //Log.d("MyLog","Sign in result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                //получаем аккаунт, который несет в себе информацию, которую мы добавили
                //отслеживаем ошибки
                if (account != null) {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                    //проверка что пользователь ввел значение в поле ввода
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
                //выводим ошибку
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this, {
            adapter.updateAdapter(it)
            rootElement.mainContent.tvEmpty.visibility = if(it.isEmpty()) View.VISIBLE else  View.GONE
        })
    }

    private fun bottomMenuOnClick() = with(rootElement) {
        mainContent.bNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.id_new_ad -> {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java)
                    startActivity(i)
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_favs -> {
                    firebaseViewModel.loadMyFavs()
                }
                R.id.id_home -> {
                    firebaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.def)
                }
            }
            true
        }
    }//слушатель нажатий кнопок меню


    private fun init() {
        setSupportActionBar(rootElement.mainContent.toolbar)
        //подключение toolbar
        val toggle = ActionBarDrawerToggle(
            this,
            rootElement.drawerLayout,
            rootElement.mainContent.toolbar,
            R.string.open,
            R.string.close
        )
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener(this)
        tvAccount = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }


    private fun initRecyclerView() {
        rootElement.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { //слушатели нажатий кнопок меню
            R.id.id_my_ads -> {
                Toast.makeText(this, "Presed id_my_ads", Toast.LENGTH_LONG).show()
            }
            R.id.id_courses -> {
                Toast.makeText(this, "Presed id_courses", Toast.LENGTH_LONG).show()
            }
            R.id.id_tutoring -> {
                Toast.makeText(this, "Presed id_tutoring", Toast.LENGTH_LONG).show()
            }
            R.id.id_vacancies -> {
                Toast.makeText(this, "Presed id_vacancies", Toast.LENGTH_LONG).show()
            }
            R.id.id_ed_mat -> {
                Toast.makeText(this, "Presed id_ed_mat", Toast.LENGTH_LONG).show()
            }
            R.id.id_help -> {
                Toast.makeText(this, "Presed id_help", Toast.LENGTH_LONG).show()
            }
            R.id.id_internship_practice -> {
                Toast.makeText(this, "Presed id_internship_practice", Toast.LENGTH_LONG).show()
            }
            R.id.id_sign_up -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }
            R.id.id_sign_in -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }
            R.id.id_sign_out -> {
                if(mAuth.currentUser?.isAnonymous == true){
                    rootElement.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutG()
                //выход из гугл аккаунта

            }
        }
        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    fun uiUpdate(user: FirebaseUser?) {

         if (user == null) {
           dialogHelper.accHelper.signInAnonymously(object: AccountHelper.Listener{
               override fun onComplete() {
                   tvAccount.text = "Гость"
               }

           })
        } else  if (user.isAnonymous) {
             tvAccount.text = "Гость"
        }else if (!user.isAnonymous){
            tvAccount.text = user.email
        }

        //отображение пользователя (email), либо просьбу войти
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
    }

    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteItem(ad)
    }

    override fun onAdViewed(ad: Ad) {
        firebaseViewModel.adViewed(ad)
    }

    override fun onFavClicked(ad: Ad) {
        firebaseViewModel.onFavClick(ad)
    }


}