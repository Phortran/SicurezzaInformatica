<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html >
<html lang="eng">


<script type="text/javascript">
	function CheckAndSubmit() {

		//document.getElementById("load").style.visibility = "hidden"; 
		var uploadForm = document.getElementById("form1");
		var uploadFile = document.getElementById("sender");

		if (uploadFile.value.length == 0) {
			alert("Please specify the path of the file to analyse!");
			//location.reload();
			//document.getElementById('load').hidden = true;
			//return document.uploadForm.reset();
		} else {
			
			document.getElementById("load").style.visibility = "visible";
			return document.uploadForm.submit();
		}
		
	}
</script>





<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">

<title>SearchVulnerabilities</title>

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

<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

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

			<!-- /.navbar-collapse -->
		</div>
		<!-- /.container-fluid -->
	</nav>

	<!-- Header -->
	<header>
		<div class="container">
			<div class="row">

				

				<div class="col-lg-8 col-lg-offset-2">



					<input type="file" id="sender" name="sender" form="form1"
						class="filestyle" data-target="load"
						data-iconName="glyphicon-plus" data-buttonText="" required>
					<!-- <img class="img-responsive" src="img/Senzanome1.png" align="top">-->
					<img class="img-responsive" src="img/Senzanome2.png" align="top">
					<div class="btn-group">

						<form action="ApkSSL" method="post" id="form1"
							enctype="multipart/form-data"></form>
							
						<a href="result.jsp"><button id="test" form="form1" data-target="#load"
								data-toggle="modal" class="btn btn-primary" onclick="CheckAndSubmit()">Analyse</button></a>

						<button type="button" class="btn btn-danger dropdown-toggle"
							data-toggle="dropdown" aria-expanded="false">
							Option <span class="caret"></span>
						</button>
						<!--  <input type="hidden" name="path" value="${pageContext.request.contextPath}" form="form1"/>-->
						<ul class="dropdown-menu " role="menu">
							<li>
								<p class="text-primary">
									<font size="3" face="Arial"> <input type="radio" id="simple"
										name="simple" value="simple" form="form1"  />Simple Analysis
									</font>
								</p>

							</li>
							<li>
								<p class="text-primary">
									<font size="3" face=""><input type="radio" id="complex"
										 name="complex" value="complex" form="form1" /> Complex
										Analysis</font>
								</p>

							</li>
						</ul>



					</div>

				</div>
				<div id="load" style="display: none" class="modal fade"
					role="dialog" tabindex="-1" aria-labelledby="loadLabel"
					aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<h4 class="modal-title" id="title">
									<font face="Comic sans" color="black">Loading</font>
								</h4>
							</div>
							<div class="model-body">
								<div class="progress">
									<div
										class="progress-bar progress-bar-striped active progress-bar-info"
										role="progressbar" aria-valuenow="40" aria-valuemin="0"
										aria-valuemax="100" style="width: 100%"></div>
								</div>
							</div>
						</div>
					</div>

				</div>

				
			</div>
		</div>
	</header>
	<footer class="text-center">
		<div class="footer-above">
			<div class="container">
				<div class="row">
					<div class="footer-col col-md-4">
						<img class="img-responsive" src="img/logotv.png" height="142"
							width="72" style="float: left;">
						<h3>Location</h3>
						<p>
							Università Tor Vergata<br>Roma
						</p>
					</div>
					<div class="footer-col col-md-4">
						<h3>Source Code</h3>
						<ul class="list-inline">

							<li><a
								href="https://github.com/Phortran/SicurezzaInformatica"
								class="btn-social btn-outline" style="float: center;"><i
									class="fa fa-fw fa-github"></i></a></li>


						</ul>
					</div>
					<div class="footer-col col-md-4">
						<h3>Developer</h3>
						<p>Gabriele Santi email: neonoetica@gmail.com</p>
						<p>Alessandro Valenti email: alessandro.valenti1991@gmail.com</p>
					</div>
				</div>
			</div>
		</div>
		<div class="footer-below">
			<div class="container">
				<div class="row">
					<div class="col-lg-12"></div>
				</div>
			</div>
		</div>
	</footer>

	<!-- Scroll to Top Button (Only visible on small and extra-small screen sizes) -->
	<div class="scroll-top page-scroll visible-xs visible-sm">
		<a class="btn btn-primary" href="#page-top"> <i
			class="fa fa-chevron-up"></i>
		</a>
	</div>


	<!-- jQuery -->
	<script src="js/jquery.js"></script>
	<script type="text/javascript" src="js/bootstrap-filestyle.js">
		
	</script>
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