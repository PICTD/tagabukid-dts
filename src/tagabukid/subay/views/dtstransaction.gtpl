<%
    /*
    def entity = [:];
    entity.paidby = 'FLORES, WORGIE';    
    entity.paidbyaddress = 'CEBU CITY';
    entity.items = [];
    entity.items << [ item: [code:'B11', title: 'MAYORS PERMIT'], amount: 560.0 ];    
    entity.items << [ item: [code:'B12', title: 'BUSINESS PERMIT'], amount: 570.0 ];
    entity.totalcash = 500.0
    entity.paymentitems = [];
    entity.paymentitems << [ type:'CHECK', particulars: 'BANK', amount: 560.0 ];    
    entity.paymentitems << [ type:'CHECK', particulars: 'BANK', amount: 570.0 ];
    */
    def df = new java.text.DecimalFormat("#,##0.00")
%>

<table width="100%" cellpadding="0" >
    <tr>
        <td><font size="5"><b>Transaction Mode</b></font></td>
        <td>:</td>
        <td><font size="6"><b>${entity.mode}</b></font></td>
    </tr>
    <tr>
        <td><font size="5"><b>Transaction Date</b></font></td>
        <td>:</td>
        <td><font size="6"><b>${entity.txndate}</b></font></td>
    </tr>
    <tr>
        <td><font size="5"><b>Prepared By</b></font></td>
        <td>:</td>
        <td><font size="6"><b>${entity.preparedbyname}</b></font></td>
    </tr>
</table>
<br>

<table width="100%">
    <tr>
        <th>DIN</th>
        <th>Title</th>
        <th>Date Created</th>
        <th>Created By</th>
        <th>Message</th>
    </tr>
    <%entity.document.each{ %>
    <tr>
        <td>${it?.din}</td>
        <td>${it?.title}</td>
        <td>${it?.recordlog.datecreated}</td>
        <td>${it?.recordlog.createdbyuser}</td>
        <td>${(it?.message) ? it.message : ''}</td>
    </tr>
    <%}%>
</table>
 
