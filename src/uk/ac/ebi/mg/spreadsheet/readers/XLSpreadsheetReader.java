package uk.ac.ebi.mg.spreadsheet.readers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import uk.ac.ebi.mg.spreadsheet.SpreadsheetReader;

public class XLSpreadsheetReader implements SpreadsheetReader
{
 private static DateFormat dateFormat;
 
 private int lineNo=0;
 private int maxRow=0;
 private Workbook workbook;
 private Sheet sheet;

 public XLSpreadsheetReader(Workbook wb)
 {
  workbook = wb;
  
  sheet = wb.getSheetAt(0);
  
  maxRow = sheet.getLastRowNum();
 }

 @Override
 public int getLineNumber()
 {
  return lineNo;
 }

 @Override
 public List<String> readRow(List<String> accum)
 {
  if( lineNo > maxRow )
   return null;

  if( accum == null )
   accum = new ArrayList<String>();
  else
   accum.clear();
  
  Row row = sheet.getRow(lineNo++);

  if( row == null )
   return accum;
  
  int lcn = row.getLastCellNum();
  
  for( int j=0; j <= lcn; j++ )
  {
   Cell c = row.getCell(j, Row.RETURN_BLANK_AS_NULL);
   
   if( c != null )
   {
    
    if( c.getCellType() == Cell.CELL_TYPE_BOOLEAN )
     accum.add( String.valueOf(c.getBooleanCellValue()) );
    else if( c.getCellType() == Cell.CELL_TYPE_NUMERIC )
    {
     if(DateUtil.isCellDateFormatted(c))
     {
      if( dateFormat == null )
       dateFormat = new SimpleDateFormat(dateTimeFormat);
      
      accum.add(dateFormat.format(c.getDateCellValue()) );
     }
     else
      accum.add( String.valueOf(c.getNumericCellValue()) );
    }
    else if( c.getCellType() == Cell.CELL_TYPE_FORMULA )
    {
     try
     {
      accum.add(String.valueOf(c.getNumericCellValue()));
     }
     catch( Throwable t )
     {
      try
      {
       accum.add(String.valueOf(c.getStringCellValue()));
      }
      catch( Throwable t1 )
      {
       try
       {
        accum.add(String.valueOf(c.getBooleanCellValue()));
       }
       catch( Throwable t3 )
       {
        accum.add(c.getCellFormula());
       }
      }
     }
    }
    else
     accum.add(c.getStringCellValue());
   }
   else
    accum.add("");
   
  }
  

  if( lineNo == maxRow )
   try
   {
    workbook.close();
   }
   catch(IOException e)
   {
    e.printStackTrace();
   }
  
  return accum;
 }

}
