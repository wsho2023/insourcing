main

Sub main() 
	Dim oApp
	Dim objShell
	Set objShell = Wscript.CreateObject("WScript.shell")
	Set oApp = CreateObject("Excel.Application")
	oApp.Visible = False	'Excelは非表示にする

	'引数のチェック、ファイルを開く
	Set WshArguments = WScript.Arguments
	Set WshNamed = WshArguments.Named
	'msgbox WshNamed("file")
	'msgbox WshNamed("method")
	'msgbox WshNamed("importFilePath")
	If WshNamed.Exists("file") Then
		curDir = objShell.CurrentDirectory
		file = curDir & "\" & WshNamed("file")
		oApp.Workbooks.Open file 'ファイルを開く
		'method: 実行するSubルーチン outfname / msg: マクロへ渡す引数値
		oApp.Run WshNamed("method"), WshNamed("importFilePath")
	Else
		Msgbox "ファイルが見つかりません。"
	End If
End Sub

