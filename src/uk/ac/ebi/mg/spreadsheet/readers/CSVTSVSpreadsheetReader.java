package uk.ac.ebi.mg.spreadsheet.readers;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.mg.spreadsheet.SpreadsheetReader;

import com.pri.util.StringUtils;

public class CSVTSVSpreadsheetReader implements SpreadsheetReader
{
 String text;
 String columnSep="\t";
 boolean xlWrap;
 
 int cpos=0;
 int lpos;
 int ln=0;
 
 int textLen;
 
 public CSVTSVSpreadsheetReader( String text, char sep )
 {
  this(text,sep,true);
 }

 public CSVTSVSpreadsheetReader( String text, char sep, boolean xl )
 {
  textLen = text.length();
  xlWrap=xl;
  
  while( cpos < textLen )
  {
   if( text.charAt(cpos) == '\r' )
    cpos++;
   else if( text.charAt(cpos) == '\n' )
   {
    ln++;
    cpos++;
   }
   else
    break;
  }
  
  if( sep == '\0' )
  {  // looking for column separator
   int commaPos = text.indexOf(',',cpos);
   int tabPos = text.indexOf('\t',cpos);
   
   commaPos = commaPos==-1?Integer.MAX_VALUE:commaPos;
   tabPos = tabPos==-1?Integer.MAX_VALUE:tabPos;
   
   if( commaPos < tabPos )
    columnSep = ",";
  }
  else
   columnSep = new String( new char[] {sep} );
  
  this.text = text;
 }
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.biostd.pagetab.SpreadsheetReader#getLineNumber()
  */
 @Override
 public int getLineNumber()
 {
  return ln;
 }
 
 public int getCurrentPosition()
 {
  return cpos;
 }
 
 public int getLineBeginPosition()
 {
  return lpos;
 }
 
 /* (non-Javadoc)
  * @see uk.ac.ebi.biostd.pagetab.SpreadsheetReader#readRow(java.util.List)
  */
 @Override
 public List<String> readRow( List<String> accum )
 {
  String line = readLine();
  
  if( line == null )
   return null;
  
  if( accum == null )
   accum = new ArrayList<String>(50);
  else
   accum.clear();

  while( true ) // Reading cell that contains new line symbols (\n)
  {
   if( xlWrap )
   {
    if( StringUtils.splitExcelString(line, columnSep, accum) )
     break;
   }
   else
   {
    int ptr=0;
    
    while( ptr < line.length() )
    {
     int nptr = line.indexOf(columnSep, ptr);
     
     if( nptr < 0 )
     {
      if( ptr == 0 )
       accum.add(line);
      else
       accum.add( line.substring(ptr) );
      
      break;
     }
     
     accum.add( line.substring(ptr,nptr) );
     ptr=nptr+columnSep.length();     
    }
   
    break;
   }
   
   accum.clear();
   
   String auxline = readLine();
   
   if( auxline == null )
    return null;

   line = line+auxline;
  }
 
  return accum;
 }
 
 public String readLine( )
 {
  if( cpos >= textLen )
   return null;
  
  lpos = cpos;
  
  ln++;

 
  int pos = text.indexOf('\n', cpos);

  String line = null;
  
  if(pos == -1)
  {
   line=text.substring(cpos);
   cpos=text.length();
  }
  else
  {
   int tpos = cpos;   
   cpos = pos + 1;

   if( text.charAt( pos-1 ) == '\r')
    pos--;
   
   line=text.substring(tpos,pos);
  }

  return line;
 }

}