<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html >
<html>




<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>Results</title>

<!-- Bootstrap Core CSS - Uses Bootswatch Flatly Theme: http://bootswatch.com/flatly/ -->
<link href="css/bootstrap.min.css" rel="stylesheet">

<!-- Custom CSS -->
<link href="css/freelancer.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="font-awesome/css/font-awesome.min.css" rel="stylesheet"
	type="text/css">
<link href="http://fonts.googleapis.com/css?family=Montserrat:400,700"
	rel="stylesheet" type="text/css">
<link
	href="http://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic"
	rel="stylesheet" type="text/css">



<!-- jQuery -->
<script src="js/jquery.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="js/bootstrap.min.js"></script>

<!-- Plugin JavaScript -->
<script
	src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
<script src="js/classie.js"></script>
<script src="js/cbpAnimatedHeader.js"></script>

<!-- Contact Form JavaScript -->
<script src="js/jqBootstrapValidation.js"></script>
<script src="js/contact_me.js"></script>

<!-- Custom Theme JavaScript -->
<script src="js/freelancer.js"></script>

</head>

<body id="page-top" class="index">

	<!-- Navigation -->
	<nav class="navbar navbar-default navbar-fixed-top">
	<div class="container">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header page-scroll">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="home.jsp">HOME</a>
		</div>

		<!-- Collect the nav links, forms, and other content for toggling -->
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav navbar-right">
				<li class="hidden"><a href="#page-top"></a></li>
				<li class="page-scroll">
					<button class="btn btn-principal  btn-lg btn-fixed-top"
						data-toggle="modal" data-target="#myModal"
						style="background-color: #2c3e50; color: white;">Time
						Stats</button>
				</li>


			</ul>
		</div>
		<!-- /.navbar-collapse -->
	</div>
	<!-- /.container-fluid --> </nav>

	<!-- Header -->
	<header>
	<div class="container">
		<div class="row">


			<!-- Modal -->
			<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog modal-primary">
					<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal"
								aria-hidden="true">&times;</button>
							<h4 class="modal-title" id="myModalLabel">
								<font face="Comic sans" color="black">Time Stats.</font>
							</h4>
						</div>
						<div class="modal-body">
							<p class="text-center">
								<font face="sans-serif"> Time for:</font>
							</p>
							<p class="text-center">
								<b><font face="Arial" color="black"> Loading classes:
										<c:out value="${timeload}" /> seconds.
								</font></b>
							</p>
							<p class="text-center">
								<b> <font face="Arial" color="black"> Reachability
										analysis: <c:out value="${timereachability}" /> seconds.</font></b>
							</p>
							<p class="text-center">
								<b><font face="sans-serif" color="black">
										Vulnerability analysis: <c:out value="${timeanalysis}" />
										seconds.
								</font></b>
							</p>

						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-default"
								data-dismiss="modal">Close</button>
								<a href="<c:out value="${pdf}"/>">Save Table</a>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.modal -->
			<P class="text-center text-default">
				<font face="glyphicons-halflings" size="6"><c:out
						value="${manifest} " /></font>
			</P>

			<div class="panel panel-info" id ="panel_info">
				<div class="panel-heading " id="panel_head">Vulnerability
					Results</div>
				<!-- /.panel-heading -->
				<div class="panel-body " id="panel_body">
					<div class="table-responsive">
						<table class="table table-striped table-bordered table-hover"
							id="panel_table">
							<thead>
								<tr class="danger">
									<th>#</th>
									<th class="text-center">Vulnerability</th>
									<th class="text-center">Vulnerability Location Class</th>
									<th class="text-center">Vulnerability Location Method</th>
									<th class="text-center" >Result</th>

								</tr>
							</thead>
							<tbody>
								<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
								<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
								
									<c:if test="${not empty table}">
										<c:forEach var="c" items="${table}">


											<tr>

												<td class="text-info">${c.getid()}</td>
												<td class="text-info">${c.getApkVulnerabilty()}</td>

												<td class="text-info">${c.getclass()}</td>
												<td class="text-info">${c.getmethod()}</td>

												<td  id="result" ><a class="text" style="text-decoration:none;" >${c.getColorResult()}</a></td>
											</tr>
										</c:forEach>
								
									</c:if>
						

							</tbody>
						</table>
					</div>
					<!-- /.table-responsive -->
				</div>
				<!-- /.panel-body -->
			</div>
		
			<nav>
			<ul class="pager">
				<li><a href="SearchVulnerabilities.jsp"><h2>
							Back <span class="label label-default"></span>
						</h2> </a></li>

			</ul>
			</nav>


		</div>


		<img class="img-responsive" src="img/Senzanome.png" alt="">
	</div>
	</header>

	<!-- Scroll to Top Button (Only visible on small and extra-small screen sizes) -->
	<div class="scroll-top page-scroll visible-xs visible-sm">
		<a class="btn btn-primary" href="#page-top"> <i
			class="fa fa-chevron-up"></i>
		</a>
	</div>

<!-- jQuery -->
	<script src="js/jquery.js"></script>
	<script type="text/javascript" src="js/bootstrap-filestyle.js"> </script>
	<!-- Bootstrap Core JavaScript -->
	<script src="js/bootstrap.min.js"></script>

	<!-- Plugin JavaScript -->
	<script
		src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
	<script src="js/classie.js"></script>
	<script src="js/cbpAnimatedHeader.js"></script>

	<!-- Contact Form JavaScript -->
	<script src="js/jqBootstrapValidation.js"></script>
	<script src="js/contact_me.js"></script>

	<!-- Custom Theme JavaScript -->
	<script src="js/freelancer.js"></script>

</body>

</html>