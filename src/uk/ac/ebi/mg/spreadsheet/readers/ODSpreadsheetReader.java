package uk.ac.ebi.mg.spreadsheet.readers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;

import uk.ac.ebi.mg.spreadsheet.SpreadsheetReader;

public class ODSpreadsheetReader implements SpreadsheetReader
{

 private static DateFormat dateFormat;
 
 private int lineNo=0;
 private int maxRow=0;
 private SpreadsheetDocument document;
 private Table table;

 
 public ODSpreadsheetReader(SpreadsheetDocument doc)
 {
  document = doc;
  table = doc.getSheetByIndex(0);
  
  maxRow = table.getRowCount();
 }

 @Override
 public int getLineNumber()
 {
  return lineNo;
 }

 @Override
 public List<String> readRow(List<String> accum)
 {
  if(lineNo > maxRow)
   return null;

  accum.clear();

  Row r = table.getRowByIndex(lineNo++);

  if(r != null)
  {

   int lcn = r.getCellCount();

   for(int j = 0; j < lcn; j++)
   {
    Cell c = r.getCellByIndex(j);

    if(c != null)
    {
     if("date".equals(c.getValueType()))
     {
      if( dateFormat == null )
       dateFormat = new SimpleDateFormat(dateTimeFormat);

      accum.add(dateFormat.format( c.getDateValue().getTime()) );
     }
     else
      accum.add(c.getStringValue());
    }
    else
     accum.add("");

   }

  }
  
  if( lineNo == maxRow )
   document.close();

  return accum;
 }

}
