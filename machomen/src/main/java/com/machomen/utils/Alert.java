package com.machomen.utils;

public class Alert {

	// Alerta tipo información
	public static String sweetAlertInfo(String text) {
		return sweetAlert("Información", text, "info");
	}

	// Alerta tipo éxito
	public static String sweetAlertSuccess(String text) {
		return sweetAlert("Exitoso", text, "success");
	}

	// Alerta tipo error
	public static String sweetAlertError(String text) {
		return sweetAlert("Error", text, "error");
	}

	// Alerta genérica con título, texto y tipo de ícono
	public static String sweetAlert(String title, String msg, String icon) {
		String scriptText = """
				<script>
				    Swal.fire({
				        title: '%s',
				        text: '%s',
				        icon: '%s'
				    });
				</script>
				""";
		return String.format(scriptText, title, msg, icon);
	}

	// Notificación tipo toast (arriba a la derecha)
	public static String sweetToast(String title, String icon, int timer) {
		String scriptText = """
				<script>
				    const Toast = Swal.mixin({
				        toast: true,
				        position: 'top-end',
				        showConfirmButton: false,
				        timer: %d,
				        timerProgressBar: true,
				        didOpen: (toast) => {
				            toast.onmouseenter = Swal.stopTimer;
				            toast.onmouseleave = Swal.resumeTimer;
				        }
				    });
				    Toast.fire({
				        icon: '%s',
				        title: '%s'
				    });
				</script>
				""";
		return String.format(scriptText, timer, icon, title);
	}

	// Alerta con imagen personalizada
	public static String sweetImageUrl(String title, String text, String imageUrl) {
		String scriptText = """
				<script>
					Swal.fire({
						title: '%s',
						text: '%s',
						imageUrl: '%s',
						imageWidth: 400,
						imageHeight: 400,
						imageAlt: 'Imagen de bienvenida',
						confirmButtonColor: '#9376E0',
						confirmButtonText: 'OK',
						customClass: {
							image: 'rounded-circle'
						}
					});
				</script>
				""";
		return String.format(scriptText, title, text, imageUrl);
	}

	// Alerta de error con HTML interno
	public static String sweetAlertErrorHtml(String mensajeHtml) {
		String scriptText = """
				<script>
					Swal.fire({
						icon: 'error',
						title: 'Error',
						html: '%s'
					});
				</script>
				""";
		return String.format(scriptText, mensajeHtml);
	}

	// Alerta genérica con contenido HTML personalizado
	public static String sweetAlertHtml(String title, String html, String icon) {
		String scriptText = """
				<script>
				    Swal.fire({
				        title: '%s',
				        html: '%s',
				        icon: '%s'
				    });
				</script>
				""";
		return String.format(scriptText, title, html, icon);
	}
}
