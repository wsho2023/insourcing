main

Sub main() 
	Dim oApp
	Dim objShell
	Set objShell = Wscript.CreateObject("WScript.shell")
	Set oApp = CreateObject("Excel.Application")
	oApp.Visible = False	'Excel�͔�\���ɂ���

	'�����̃`�F�b�N�A�t�@�C�����J��
	Set WshArguments = WScript.Arguments
	Set WshNamed = WshArguments.Named
	'msgbox WshNamed("file")
	'msgbox WshNamed("method")
	'msgbox WshNamed("importFilePath")
	If WshNamed.Exists("file") Then
		curDir = objShell.CurrentDirectory
		file = curDir & "\" & WshNamed("file")
		oApp.Workbooks.Open file '�t�@�C�����J��
		'method: ���s����Sub���[�`�� outfname / msg: �}�N���֓n�������l
		oApp.Run WshNamed("method"), WshNamed("importFilePath")
	Else
		Msgbox "�t�@�C����������܂���B"
	End If
End Sub

