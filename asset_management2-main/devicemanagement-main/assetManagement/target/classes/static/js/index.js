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
