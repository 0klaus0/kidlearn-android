Attribute VB_Name = "模块1"
' ============================================
' 工时分配宏 - 优化版本
' 优化内容：
' 1. 使用数组批量读写，减少单元格访问次数
' 2. 使用Application.StatusBar显示进度
' 3. 增加更多提示信息
' 4. 优化日期解析逻辑
' 5. 缓存项目信息，减少重复计算
' ============================================

Sub button1()
    Dim ws As Worksheet
    Dim projectCount As Long
    Dim row1 As Long, row2 As Long
    Dim column1 As Long, column2 As Long
    Dim i As Long, j As Long, k As Long
    Dim rowsProcessed As Long
    Dim startRow As Long, endRow As Long
    Dim processedRanges As String
    Dim startTime As Double
    
    ' ===== 性能优化：关闭屏幕更新和计算 =====
    Application.ScreenUpdating = False
    Application.Calculation = xlCalculationManual
    Application.EnableEvents = False
    Application.StatusBar = "正在初始化工时分配..."
    
    startTime = Timer
    
    ' ===== 检查工作表 =====
    If ActiveSheet.Name <> ActiveSheet.Cells(1, 1).Value Then
        Application.StatusBar = False
        Application.ScreenUpdating = True
        Application.Calculation = xlCalculationAutomatic
        Application.EnableEvents = True
        MsgBox "请打开配套的高新研发台账模板后再尝试！", 64, "提示"
        Exit Sub
    End If
    
    Set ws = ThisWorkbook.Sheets(ActiveSheet.Name)
    
    ' ===== 读取基本参数 =====
    row1 = 11
    projectCount = ActiveSheet.Cells(2, 2).Value
    column1 = 13
    column2 = column1 + projectCount - 1
    row2 = row1 + ActiveSheet.Cells(2, 4).Value - 1
    
    ' ===== 参数验证 =====
    If Not IsNumeric(projectCount) Or projectCount <= 0 Then
        Application.StatusBar = False
        Application.ScreenUpdating = True
        Application.Calculation = xlCalculationAutomatic
        Application.EnableEvents = True
        MsgBox "项目数量(B2)无效，请检查！", 64, "提示"
        Exit Sub
    End If
    
    ' ===== 设置提示颜色 =====
    ActiveSheet.Cells(4, 8).Font.Color = RGB(255, 0, 0)
    
    ' ===== 缓存权重数据到数组 =====
    Application.StatusBar = "正在加载项目权重..."
    Dim weights As Object
    Set weights = CreateObject("Scripting.Dictionary")
    
    Dim weightKeys() As Variant
    Dim weightValues() As Variant
    ReDim weightKeys(1 To projectCount)
    ReDim weightValues(1 To projectCount)
    
    Dim validProjectCount As Long
    validProjectCount = 0
    
    For k = 1 To projectCount
        Dim projKey As Variant
        Dim projWeight As Variant
        projKey = ActiveSheet.Cells(8, column1 + k - 1).Value
        projWeight = ActiveSheet.Cells(6, column1 + k - 1).Value
        
        If IsEmpty(projKey) Or IsError(projKey) Then
            GoTo NextProject
        End If
        
        Dim keyStr As String
        keyStr = Trim(CStr(projKey))
        If keyStr = "" Or keyStr = "0" Then
            GoTo NextProject
        End If
        
        If Not weights.Exists(keyStr) Then
            weights.Add keyStr, projWeight
            validProjectCount = validProjectCount + 1
        End If
NextProject:
    Next k
    
    ' ===== 缓存项目信息（成员、期间、编号）到数组 =====
    Application.StatusBar = "正在缓存项目信息..."
    Dim projectMembersArr() As Variant
    Dim projectPeriodArr() As Variant
    Dim projectNumArr() As Variant
    Dim projectStartDateArr() As Variant
    Dim projectEndDateArr() As Variant
    
    ReDim projectMembersArr(column1 To column2)
    ReDim projectPeriodArr(column1 To column2)
    ReDim projectNumArr(column1 To column2)
    ReDim projectStartDateArr(column1 To column2)
    ReDim projectEndDateArr(column1 To column2)
    
    For j = column1 To column2
        Dim periodStr As String
        Dim periodVal As Variant
        periodVal = ws.Cells(10, j).Value
        
        ' 缓存项目成员和编号
        projectMembersArr(j) = CStr(ws.Cells(4, j).Value)
        projectNumArr(j) = CStr(ws.Cells(8, j).Value)
        projectPeriodArr(j) = periodVal
        
        ' 预解析日期期间
        If IsEmpty(periodVal) Or IsError(periodVal) Then
            projectStartDateArr(j) = Empty
            projectEndDateArr(j) = Empty
            GoTo NextColumnCache
        End If
        
        periodStr = CStr(periodVal)
        If Trim(periodStr) = "" Or periodStr = "0" Then
            projectStartDateArr(j) = Empty
            projectEndDateArr(j) = Empty
            GoTo NextColumnCache
        End If
        
        ' 解析日期期间
        Dim posRi As Long
        Dim sdate As String, edate As String
        Dim startDate As Date, endDate As Date
        
        posRi = InStr(periodStr, ChrW(26085))  ' "日"
        If posRi > 0 And posRi < Len(periodStr) Then
            sdate = Left(periodStr, posRi - 1)
            ' 从"日"之后找到下一个数字
            Dim pos2 As Long
            For pos2 = posRi + 1 To Len(periodStr)
                Dim ch As String
                ch = Mid(periodStr, pos2, 1)
                If ch >= "0" And ch <= "9" Then
                    Exit For
                End If
            Next pos2
            If pos2 <= Len(periodStr) Then
                edate = Mid(periodStr, pos2)
                ' 替换年月日为横线
                sdate = Replace(Replace(Replace(sdate, ChrW(24180), "-"), ChrW(26376), "-"), ChrW(26085), "")
                edate = Replace(Replace(Replace(edate, ChrW(24180), "-"), ChrW(26376), "-"), ChrW(26085), "")
                On Error Resume Next
                startDate = CDate(sdate)
                endDate = CDate(edate)
                If Err.Number = 0 Then
                    projectStartDateArr(j) = Format(startDate, "yyyy-mm")
                    projectEndDateArr(j) = Format(endDate, "yyyy-mm")
                Else
                    projectStartDateArr(j) = Empty
                    projectEndDateArr(j) = Empty
                End If
                On Error GoTo 0
            Else
                projectStartDateArr(j) = Empty
                projectEndDateArr(j) = Empty
            End If
        Else
            projectStartDateArr(j) = Empty
            projectEndDateArr(j) = Empty
        End If
NextColumnCache:
    Next j
    
    ' ===== 批量读取数据到数组 =====
    Application.StatusBar = "正在读取数据..."
    Dim dataArr As Variant
    If row2 >= row1 Then
        dataArr = ws.Range(ws.Cells(row1, 2), ws.Cells(row2, 10)).Value
    Else
        ReDim dataArr(1 To 1, 1 To 9)
    End If
    
    ' ===== 准备输出数组 =====
    Dim outputProjectList() As String
    Dim outputValues() As Variant
    ReDim outputProjectList(row1 To row2)
    ReDim outputValues(row1 To row2, column1 To column2)
    
    ' 初始化输出数组
    For i = row1 To row2
        outputProjectList(i) = ""
        For j = column1 To column2
            outputValues(i, j) = ""
        Next j
    Next i
    
    ' ===== 主处理循环 =====
    rowsProcessed = 0
    startRow = 0
    endRow = 0
    processedRanges = ""
    
    Dim totalRows As Long
    totalRows = row2 - row1 + 1
    
    For i = row1 To row2
        ' 每50行更新一次状态栏，减少开销
        If i Mod 50 = 0 Then
            Application.StatusBar = "正在处理第 " & i & " 行，共 " & row2 & " 行 (" & Format((i - row1 + 1) / totalRows, "0%") & ")..."
            DoEvents
        End If
        
        Dim memberDate1 As Variant
        Dim arrRow As Long
        arrRow = i - row1 + 1
        
        ' 从数组读取日期
        memberDate1 = dataArr(arrRow, 1)
        
        ' 检查是否为日期
        If Not IsDate(memberDate1) Then
            If startRow > 0 Then
                processedRanges = processedRanges & "第 " & startRow & " 行到第 " & endRow & " 行" & vbCrLf
                ws.Cells(i, 5).Value = "=Sum(E" & startRow & ":E" & endRow & ")"
                startRow = 0
                endRow = 0
            End If
            GoTo NextRow
        End If
        
        If startRow = 0 Then
            startRow = i
        End If
        
        ' 从数组读取成员名称和数量
        Dim memberName As String
        Dim quantity As Double
        Dim memberDate As String
        
        memberName = CStr(dataArr(arrRow, 2))
        memberDate = Format(CDate(memberDate1), "yyyy-mm")
        quantity = dataArr(arrRow, 5)
        
        ' 匹配项目
        Dim projectList As String
        Dim totalWeight As Double
        projectList = ""
        totalWeight = 0
        
        For j = column1 To column2
            ' 使用缓存的项目信息
            If IsEmpty(projectStartDateArr(j)) Then
                GoTo NextColumnOptimized
            End If
            
            Dim projStart As String
            Dim projEnd As String
            projStart = projectStartDateArr(j)
            projEnd = projectEndDateArr(j)
            
            Dim projNum As String
            projNum = projectNumArr(j)
            
            ' 检查成员是否在项目中
            If InStr(projectMembersArr(j), memberName) > 0 Then
                If memberDate >= projStart And memberDate <= projEnd Then
                    If projectList = "" Then
                        projectList = projNum
                    Else
                        projectList = projectList & ChrW(12289) & projNum  ' "、"
                    End If
                    If weights.Exists(projNum) Then
                        totalWeight = totalWeight + weights(projNum)
                    End If
                End If
            End If
NextColumnOptimized:
        Next j
        
        ' 保存项目列表
        outputProjectList(i) = projectList
        
        ' 计算并保存加权数量
        For j = column1 To column2
            Dim projectNumber1 As String
            projectNumber1 = projectNumArr(j)
            If InStr(projectList, projectNumber1) > 0 Then
                If totalWeight = 0 Then
                    outputValues(i, j) = 0
                Else
                    outputValues(i, j) = quantity * (weights(projectNumber1) / totalWeight)
                End If
            Else
                outputValues(i, j) = ""
            End If
        Next j
        
        rowsProcessed = rowsProcessed + 1
        endRow = i
NextRow:
    Next i
    
    ' ===== 批量写入结果 =====
    Application.StatusBar = "正在写入结果..."
    
    ' 写入项目列表（J列）
    For i = row1 To row2
        If outputProjectList(i) <> "" Then
            ws.Cells(i, 10).Value = outputProjectList(i)
        Else
            ws.Cells(i, 10).Value = ""
            ws.Cells(i, 5).Value = ""
        End If
    Next i
    
    ' 批量写入分配值（M列及以后）
    If row2 >= row1 Then
        ws.Range(ws.Cells(row1, column1), ws.Cells(row2, column2)).Value = _
            Application.WorksheetFunction.Index(outputValues, Evaluate("ROW(" & row1 & ":" & row2 & ")-" & row1 - 1), Evaluate("COLUMN(" & column1 & ":" & column2 & ")-" & column1 - 1))
    End If
    
    ' 处理最后的区间
    If startRow > 0 Then
        processedRanges = processedRanges & "第 " & startRow & " 行到第 " & endRow & " 行" & vbCrLf
    End If
    
    ' ===== 恢复设置 =====
    Application.StatusBar = False
    Application.ScreenUpdating = True
    Application.Calculation = xlCalculationAutomatic
    Application.EnableEvents = True
    
    ' ===== 计算耗时 =====
    Dim elapsedTime As Double
    elapsedTime = Timer - startTime
    
    ' ===== 显示完成提示 =====
    Dim msg As String
    msg = "工时分配完成！" & vbCrLf & vbCrLf
    msg = msg & "处理行数: " & rowsProcessed & " 行" & vbCrLf
    msg = msg & "有效项目数: " & validProjectCount & " 个" & vbCrLf
    msg = msg & "耗时: " & Format(elapsedTime, "0.00") & " 秒" & vbCrLf & vbCrLf
    If processedRanges <> "" Then
        msg = msg & "处理区间:" & vbCrLf & processedRanges
    End If
    MsgBox msg, 64, "工时分配完成"
    
End Sub
