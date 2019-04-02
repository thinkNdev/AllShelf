package chickenmumani.com.allshelf;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Export_Activity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.pref_export);
        saveExcelFile();

        setTitle("내 서재 내보내기");

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveExcelFile(){
        final ArrayList<Book_Item> book_list = new ArrayList<Book_Item>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();;
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference insData = FirebaseDatabase.getInstance().getReference("User_Book").child(user.getUid());
        insData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                book_list.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>)postSnapshot.getValue();
                    try{
                        book_list.add(new Book_Item(map.get("title").toString(), map.get("author").toString(), map.get("isbn").toString(), map.get("time").toString(), map.get("imgurl").toString()));
                    }
                    catch(NullPointerException event){
                        event.printStackTrace();
                    }
                }

                Workbook workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet();
                Row row = sheet.createRow(0);
                Cell cell;

                cell = row.createCell(0);
                cell.setCellValue("책 제목");

                cell = row.createCell(1);
                cell.setCellValue("지은이");

                cell = row.createCell(2);
                cell.setCellValue("ISBN");

                cell = row.createCell(3);
                cell.setCellValue("등록일자");

                cell = row.createCell(4);
                cell.setCellValue("책 이미지 URL");

                for(int i = 0; i < book_list.size(); i++){
                    row = sheet.createRow(i + 1);

                    cell = row.createCell(0);
                    cell.setCellValue(book_list.get(i).getTitle());

                    cell = row.createCell(1);
                    cell.setCellValue(book_list.get(i).getAuthor());

                    cell = row.createCell(2);
                    cell.setCellValue(book_list.get(i).getISBN());

                    cell = row.createCell(3);
                    cell.setCellValue(book_list.get(i).getTime());

                    cell = row.createCell(4);
                    cell.setCellValue(book_list.get(i).getImgUrl());
                }

                File xlsFile = new File(getExternalFilesDir(null), "mybook.xls");
                try{
                    FileOutputStream os = new FileOutputStream(xlsFile);
                    workbook.write(os); // 외부 저장소에 엑셀 파일 생성
                }catch (IOException e){
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(),xlsFile.getAbsolutePath()+"에 저장되었습니다",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SEND);
                //Uri path = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", xlsFile.getAbsolutePath());
                //Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", createImageFile());

                intent.setType("application/excel");
                //intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+xlsFile.getAbsolutePath()));

                //startActivity(Intent.createChooser(intent, "내 서재 내보내기"));
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
