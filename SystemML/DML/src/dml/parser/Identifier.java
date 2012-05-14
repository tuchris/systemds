package dml.parser;

import java.util.HashMap;

import dml.utils.LanguageException;

public abstract class Identifier extends Expression{

	private DataType _dataType;
	private ValueType _valueType;
	private long _dim1;
	private long _dim2;
	private long _rows_in_block;
	private long _columns_in_block;
	private long _nnzs;
	private FormatType _formatType;
		
	public Identifier(Identifier i){
		_dataType = i.getDataType();
		_valueType = i.getValueType();
		_dim1 = i.getDim1();
		_dim2 = i.getDim2();
		_rows_in_block = i.getRowsInBlock();
		_columns_in_block = i.getColumnsInBlock();
		_nnzs = i.getNnzs();
		_formatType = i.getFormatType();
	}
	
	public Identifier(){
		_dim1 = -1;
		_dim2 = -1;
		_dataType = DataType.UNKNOWN;
		_valueType = ValueType.UNKNOWN;
		_rows_in_block = -1;
		_columns_in_block = -1;
		_nnzs = -1;
		_output = this;
		_formatType = null;
	}
	
	public void setProperties(Identifier i){
		_dataType = i.getDataType();
		_valueType = i.getValueType();
		_dim1 = i.getDim1();
		_dim2 = i.getDim2();
		_rows_in_block = i.getRowsInBlock();
		_columns_in_block = i.getColumnsInBlock();
		_nnzs = i.getNnzs();
		_formatType = i.getFormatType();
	}
	
	public void setDimensionValueProperties(Identifier i){
			_dim1 = i.getDim1();
			_dim2 = i.getDim2();
			_dataType = i.getDataType();
			_valueType = i.getValueType();
	}
	
	public void setDataType(DataType dt){
		_dataType = dt;
	}
	
	public void setValueType(ValueType vt){
		_valueType = vt;
	}
	
	public void setFormatType(FormatType ft){
		_formatType = ft;
	}
	
	public void setDimensions(long dim1, long dim2){
		_dim1 = dim1;
		_dim2 = dim2;
	}
	public void setDimensions(InputStatement is){
		
		// check if 
		if (is.getStringParam("rows") != null) 
			_dim1 = new Long(is.getStringParam("rows"));
		if (is.getStringParam("cols") != null) 
			_dim2 = new Long(is.getStringParam("cols"));
	}
	
	
	public void setBlockDimensions(long dim1, long dim2){
		 _rows_in_block = dim1;
		_columns_in_block = dim2;
	}
	
	public void setNnzs(long nnzs){
		_nnzs = nnzs;
	}
	
	public long getDim1(){
		return _dim1;
	}
	
	public long getDim2(){
		return _dim2;
	}
	
	public DataType getDataType(){
		return _dataType;
	}
	
	public ValueType getValueType(){
		return _valueType;
	}
	
	public FormatType getFormatType(){
		return _formatType;
	}
	
	public long getRowsInBlock(){
		return _rows_in_block;
	}
	
	public long getColumnsInBlock(){
		return _columns_in_block;
	}
	
	public long getNnzs(){
		return _nnzs;
	}
	
	public void validateExpression(HashMap<String,DataIdentifier> ids) throws LanguageException {
		Identifier out = this.getOutput();
		
		if (out instanceof DataIdentifier){
			
			// set properties for Data identifer
			String name = ((DataIdentifier)out).getName();
			Identifier id = ids.get(name);
			if ( id == null )
				LiveVariableAnalysis.throwUndefinedVar(name, "");
			this.getOutput().setProperties(id);
			
			// validate IndexedIdentifier -- which is substype of DataIdentifer with index
			if (out instanceof IndexedIdentifier){
				
				// validate the row / col index bounds (if defined)
				IndexedIdentifier indexedIdentiferOut = (IndexedIdentifier)out;
				if (indexedIdentiferOut.getRowLowerBound() != null) 
					indexedIdentiferOut.getRowLowerBound().validateExpression(ids);
				if (indexedIdentiferOut.getRowUpperBound() != null) 
					indexedIdentiferOut.getRowUpperBound().validateExpression(ids);
				if (indexedIdentiferOut.getColLowerBound() != null) 
					indexedIdentiferOut.getColLowerBound().validateExpression(ids);	
				if (indexedIdentiferOut.getColUpperBound() != null) 
					indexedIdentiferOut.getColUpperBound().validateExpression(ids);
				
				// update the size of the indexed expression output
				((IndexedIdentifier)out).updateIndexedDimensions();
				
				
				
			}
							
		} else {
			this.getOutput().setProperties(out);
		}
	}
	
	public void computeDataType() throws ParseException{
				
		if ((_dim1 == 0) && (_dim2 == 0)) {
			_dataType = DataType.SCALAR;
		} else if ((_dim1 >= 1) || (_dim2 >= 1)){
			// Vector also set as matrix
			// Data type is set as matrix, if either of dimensions is -1
			_dataType = DataType.MATRIX;
		} else _dataType = DataType.UNKNOWN;	 
		
	}
	
	public void setBooleanProperties(){
		_dataType = DataType.SCALAR;
		_valueType = ValueType.BOOLEAN;
		_dim1 = 0;
		_dim2 = 0;
		_rows_in_block = 1;
		_columns_in_block = 1;
		_nnzs = 1;
		_formatType = null;
	}
	
	public void setIntProperties(){
		_dataType = DataType.SCALAR;
		_valueType = ValueType.INT;
		_dim1 = 0;
		_dim2 = 0;
		_rows_in_block = 1;
		_columns_in_block = 1;
		_nnzs = 1;
		_formatType = null;
	}
	
	
	public boolean isScalarBoolean(){
		return (_valueType == ValueType.BOOLEAN) && (_dataType == DataType.SCALAR);
	}
}
