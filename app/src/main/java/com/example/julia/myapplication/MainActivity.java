package com.example.julia.myapplication;

import android.app.Activity;
import android.content.Context;
import android.icu.util.RangeValueIterator;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText; // подключаем класс EditText
import android.widget.ListView;
import android.widget.Spinner; // подключаем класс Spinner

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    public Elements content;
     public ArrayList<String> emailList=new ArrayList<String>();
   // связывает массив данных с набором элементов TextView, из которых может состоять ListView
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private String url;// имя сайта
    private String valueSpinner="1";// глубина
    private Document doc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //создается весь интерфейс приложения
        super.onCreate(savedInstanceState);
        //передается идентификатор ресурса
        setContentView(R.layout.activity_main);
    }

    public class NewThread extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... arg) {
            try{


                // вход на сайт
                doc= Jsoup.connect(url).get();
               content=doc.select("a");// поиск всех ссылок

                emailList.clear();

                SearchEmailInDepth(1);

            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            listView.setAdapter(adapter);

        }

        protected void SearchEmailInDepth(int depth)
        {
            for(Element contents:content){

                if(depth>Integer.parseInt(valueSpinner) ) {
                    return;
                }

                if(contents.text().matches(".+@[a-z]+\\.[a-z]+"))// email
                    emailList.add(contents.text());
                else{
                    if(depth<Integer.parseInt(valueSpinner)) {
                        depth++;
                       try{

                            doc= Jsoup.connect(contents.baseUri()).get();
                            content=doc.select("a");// поиск всех ссылок
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }

                        SearchEmailInDepth(depth);
                        depth--;
                    }
                }
            }
        }



    }



    // Метод обработки нажатия на кнопку
    public void button_OnClick(View view) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


        // Получаем текстовое поле в текущей Activity
        EditText editText = (EditText) findViewById(R.id.editText);

        // Получае текст данного текстового поля
        url = editText.getText().toString();

        Spinner spinner=(Spinner) findViewById(R.id.spinner);
        valueSpinner= spinner.getSelectedItem().toString();

        listView=(ListView) findViewById(R.id.listView1);
        editText.setFocusable(false);

        new NewThread().execute();

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,emailList);

    }
}

