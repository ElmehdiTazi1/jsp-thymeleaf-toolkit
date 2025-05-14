<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/entete.jsp"%>
<!DOCTYPE html>

<html>
<head>
<title>Accueil TWS2</title>

<%@ include file="/WEB-INF/views/include/styles.jsp"%>

<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

<!-- Fichiers CSS -->
<link href="<c:url value="/resources/css/accueil.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/overlay.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/fuzzySearch.css" />" rel="stylesheet">
<link href="<c:url value="/resources/css/entete.css" />" rel="stylesheet">

</head>

<body>
	<!-- Inclusion Entete avec logo -->
	<%@include file="include/enteteTemplate.jsp"%>
	<!-- FIN inclusion Entete avec logo -->


	<div id="block_recherche" class="container-fluid">
		<form class="form-horizontal">
			<!-- DEBUT Block Liste tous les scenarios -->
			<div class="row">
				<div class="col-md-12 col-md-offset-3">
					<div id="all_scenario" class="panel panel-default">
						<div id="panel-heading" class="panel-heading col-md-12">
							<div class="row">
								<div class="col-md-4">
									<h3 id="btn_trie_par_libelle" class="panel-title btn_trie">
										Par libelle<em id='tri_par_libelle' class='material-icons'>keyboard_arrow_up</em>
									</h3>
								</div>
								<div class="col-md-3 col-md-offset-1">
									<h3 id="btn_trie_par_nombre_cas" class="panel-title btn_trie">
										Par nombre de cas
									</h3>
								</div>
								<div class="col-md-4">
									<h3 id="btn_trie_par_date" class="panel-title btn_trie">
										Par date de dernière modification
									</h3>
								</div>
							</div>
						</div>
						<div class="input-group col-md-12">
							<select id="list_all_scenario" class="form-control" size="15">
							</select>
						</div>
					</div>
				</div>
			</div>
			<!-- Fin Block Liste tous les scenarios -->

			<!-- DEBUT Block recherche par scenario + boutons -->
			<div id='block_form_recherche' class="row">
				<div class="col-md-5 col-md-offset-1">
					<div class="form-group">
						<label id="label_scenario" for="input_scenario" class="col-md-3 control-label">Scénario</label>
						<!-- Block recherche par scenario + boutons -->
						<div id="btn_ligne_recherche" class="input-group col-md-8">
							<input class="form-control typeahead" id="input_scenario" placeholder="Entrez le libellé du scénario que vous recherchez" type="search" autofocus autocomplete="off">
						</div>

					</div>
					<!-- FIN Block recherche par scenario + boutons -->

					<!-- Block recherche avancee -->
					<div id="block_recherche_avancee" class="row">
						<div id="recherche_avancee">
							<label for="input_collectivite" class="col-md-3 control-label">Collectivité</label>
							<div class="input-group col-md-8">
								<input class="form-control typeahead" id="input_collectivite" placeholder="Entrez le numero du code collectivité" type="search">
							</div>
							<label class="col-md-3 control-label" for="web_services">Web Service</label>
							<div class="input-group col-md-8">
								<div id="ws_nom" class="radio row radio-primary"></div>
								<hr>
								<div id="ws_version" class="radio radio-primary"></div>
							</div>
						</div>
					</div>
					<!-- FIN Block recherche avancee -->

				</div>

				<div id="btn_scenario" class="col-md-6">
					<div>
						<ul class="pager nav nav-pills">
							<li>
								<div id="div_recherche_avancee" class="togglebutton col-md-12">
									<label> <input id="btn_recherche_avancee" type="checkbox">Recherche Avancée
									</label>
								</div>
							</li>
							<li>
								<div id="toggle_scenario" class="togglebutton col-md-12">
								<label>
									<input id="btn_all_scenario" type="checkbox">Afficher tous les scénarios
								</label>
								</div>
							</li>
							<li><a id="btn_page_accueil" class="withripple col-md-12" href="/tws/creerScenario">Créer scénario<em class="material-icons">create</em></a></li>

						</ul>
					</div>
				</div>

			</div>
			<div class="row">
				<div id="ligne_btn_recherche_avancee" class="col-md-offset-4">
					<button id="btn_rechercher" class="btn btn-primary col-md-3 " type="button">Rechercher</button>
					<button id="btn_reset" class="btn btn-primary col-md-3 " type="button">Réinitialiser</button>
				</div>
			</div>



			<!-- Block liste des resultats -->
			<div class="row" id="resultat_recherche">
				<label class="col-md-3 control-label" for="resultat_recherche">Résultats</label>
				<div class="input-group col-md-6">
					<select id="myselect" class="form-control" size="15">

					</select>
				</div>
			</div>
			<!-- FIN Block liste des resultats -->

			<!-- Block boutons actions -->
			<div class="row">
				<div id="boutons_action" class="col-md-8 col-md-offset-3">
					<ul class="nav nav-pills">
						<li>
							<button id="btn_modifier_scenario" class="btn btn-primary" type="button">
								Modifier <em class="material-icons">create</em>
							</button>
						</li>
						<li>
							<button id="btn_supprimer_scenario" class="btn btn-primary" type="button" data-toggle="modal" data-target="#modalSupprimerScenario">
								Supprimer<em class="material-icons">delete</em>
							</button>
						</li>

						<li>
							<button id="btn_dupliquer_scenario" class="btn btn-primary" type="button">
								Dupliquer<em class="material-icons">content_copy</em>
							</button>
						</li>

						<li>
							<button id="btn_visualiser_scenario" class="btn btn-primary" type="button" data-toggle="modal" data-target="#modalChoixVisualisation">
								Visualiser<em class="material-icons">visibility</em>
							</button>
						</li>

						<li>
							<button id="btn_executer_scenario" class="btn btn-primary" type="button">
								Exécuter<em class="material-icons">play_arrow</em>
							</button>
						</li>

					</ul>
				</div>
			</div>
			<!-- FIN Block boutons actions -->
		</form>
	</div>

	<!-- Inclusion fenetre modal -->
	<%@include file="include/fenetreModalChoixDeVisualisation.jsp"%>
	<!-- FIN inclusion fenetre modal -->

	<!-- Inclusion fenetre modal -->
	<%@include file="include/fenetreModalSupprimerScenario.jsp"%>
	<!-- FIN inclusion fenetre modal -->

	<!-- Scripts Jquery / bootstrap / material-design -->
    <%@ include file="/WEB-INF/views/include/scripts.jsp"%>
	<!-- Underscore -->
	<script src="<c:url value="/resources/js/lib/underscore.js" />"></script>
	<!-- Mustache -->
	<script type="text/javascript" src="<c:url value="/resources/js/lib/mustache.min.js"/>"></script>

	<!-- Fichiers JS -->
	<!-- Ce fichier doit etre place avant tous les autres fichiers JS utilisant les variables de la forme URL_XXX -->
	<script type="text/javascript" src="<c:url value="/resources/js/util/urlController.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/util/ajaxoutil.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/util/messageUtil.js"/>"></script>

	<!-- Fuzzy Search -->
	<script src="<c:url value="/resources/js/lib/typeahead.bundle.js"/>"></script>
	<script src="<c:url value="/resources/js/lib/fuzzyset.js"/>"></script>
	<script src="<c:url value="/resources/js/lib/subsequence-search.js"/>"></script>
	<script src="<c:url value="/resources/js/fuzzysearch.js"/>"></script>
	<script src="<c:url value="/resources/js/fuzzyCodeCollectivite.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/resources/js/accueil.js"/>"></script>
</body>
</html>
