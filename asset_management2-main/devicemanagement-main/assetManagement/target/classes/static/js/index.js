var state = false;
function toggle()
{
if(state)
{
document.getElementById("id_password").setAttribute("type","password");
document.getElementById("togglePassword").style.color="black";
state = false;
}
else
{
document.getElementById("id_password").setAttribute("type","text");
document.getElementById("togglePassword").style.color="white";
state = true;
}
}
$("#category").change(function() {
        var x = $("#category").val();
        console.log(x);
        $.ajax({
			type: 'GET',
			url: '/loadNamesByCategory/' + x,
			success: function(result) {
				var result = JSON.parse(result);
				console.log(result);
				var s = '';
				for(var i = 0; i < result.length; i++) {
					s += '<option value="' + result[i] + '">' + result[i] + '</option>';
				}
				$('#deviceName').html(s);
			}
		});
      });
var dropdown = document.getElementsByClassName("dropdown-btn");
var i;

for (i = 0; i < dropdown.length; i++) {
  dropdown[i].addEventListener("click", function() {
    this.classList.toggle("active");
    var dropdownContent = this.nextElementSibling;
    if (dropdownContent.style.display === "block") {
      dropdownContent.style.display = "none";
    } else {
      dropdownContent.style.display = "block";
    }
  });
}
