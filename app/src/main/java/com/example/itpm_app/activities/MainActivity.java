package com.example.itpm_app.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.CaseMap;
import android.os.Bundle;

import com.example.itpm_app.R;
import com.example.itpm_app.databases.ITPMDataOpenHelper;
import com.example.itpm_app.pojo.TitleDataItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
//    private ArrayAdapter<String> mAdapter;
    private MainListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ListViewをレイアウトファイルから読み込む
        mListView = findViewById(R.id.main_list);
        // Adapterを作成する
//        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        mAdapter = new MainListAdapter(this, R.layout.layout_title_item, new ArrayList<TitleDataItem>());
        // ListViewにAdapterをセットする
        mListView.setAdapter(mAdapter);

        // 通常のクリックリスナーのセット
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Log.d(MainActivity.class.getSimpleName(), position + "番目のリストアイテムが押されました");
//                Intent intent = EditActivity.createIntent(MainActivity.this, String.valueOf(adapterView.getItemAtPosition(position)));
                TitleDataItem item = (TitleDataItem)adapterView.getItemAtPosition(position);
                Intent intent = EditActivity.createIntent(MainActivity.this, item.getId(), item.getTitle());
                startActivity(intent);
            }
        });
        // 長押しでのクリックリスナーのセット
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
//                Log.d(MainActivity.class.getSimpleName(), position + "番目のリストアイテムが長押しされました");
//                String title = String.valueOf(adapterView.getItemAtPosition(position));
//                mAdapter.remove(title);
//                Toast.makeText(MainActivity.this, title + "を削除しました", Toast.LENGTH_SHORT).show();
//                mAdapter.remove(item);
                TitleDataItem item = (TitleDataItem)adapterView.getItemAtPosition(position);
                // 1. データベースのインスタンスを取得する
                SQLiteDatabase db = new ITPMDataOpenHelper(MainActivity.this).getWritableDatabase();
                // 2. データベースからデータを削除する
                db.delete(ITPMDataOpenHelper.TABLE_NAME, ITPMDataOpenHelper._ID + "=" + item.getId(), null);
                // 3. データベースを閉じる
                db.close();
                // 4. 画面表示を更新する
                displayDataList();
                Toast.makeText(MainActivity.this, item.getTitle() + "を削除しました。", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // リスト表示
        displayDataList();
    }

    // リスト表示用メソッド
    private void displayDataList() {
        // アダプター内のデータをリセットする
        mAdapter.clear();
        // 新しいデータをアダプターに設定する
//        List<String> dataList = Arrays.asList("ホーム", "事業内容", "企業情報", "採用情報", "お問い合わせ");
//        List<TitleDataItem> dataList = Arrays.asList(
//                new TitleDataItem(1, "ホーム"),
//                new TitleDataItem(2, "事業内容"),
//                new TitleDataItem(3, "企業情報"),
//                new TitleDataItem(4, "採用情報"),
//                new TitleDataItem(5, "お問い合わせ")
//        );
        // 表示に使うデータを入れるリスト
        List<TitleDataItem> itemList = new ArrayList<>();
        // 読み書き用のデータベースインスタンスを取得する
        SQLiteDatabase db = new ITPMDataOpenHelper(this).getWritableDatabase();
        // データベースから欲しいデータのカーソルを取り出す
        Cursor cursor = db.query(
                ITPMDataOpenHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        // カーソルを使ってデータを取り出す
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ITPMDataOpenHelper._ID));
            String title = cursor.getString(cursor.getColumnIndex(ITPMDataOpenHelper.COLUMN_TITLE));
            itemList.add(new TitleDataItem(id, title));
        }
        // カーソルを閉じる
        cursor.close();
        // データベースを閉じる
        db.close();
        // 新しいデータをアダプターに設定する
        mAdapter.addAll(itemList);
        // アダプターにデータが変更されたことを通知する
        mAdapter.notifyDataSetChanged();
    }

    private class MainListAdapter extends ArrayAdapter<TitleDataItem> {
        private LayoutInflater layoutInflater;
        private int resource;
        private List<TitleDataItem> dataList;

        public MainListAdapter(@NonNull Context context, int resource, @NonNull List<TitleDataItem> objects) {
            super(context, resource, objects);
            this.layoutInflater = LayoutInflater.from(context);
            this.resource = resource;
            this.dataList = objects;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Nullable
        @Override
        public TitleDataItem getItem(int position) {
            return dataList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TitleDataItem item = dataList.get(position);
            TextView titleTextView;
            if (convertView == null) {
                // 新規
                convertView = layoutInflater.inflate(resource, null);
                titleTextView = convertView.findViewById(R.id.title_text_view);
                convertView.setTag(titleTextView);
            } else {
                // 再利用
                titleTextView = (TextView)convertView.getTag();
            }

            // 表示処理
            titleTextView.setText(item.getTitle());

            return convertView;
        }

    }
}
