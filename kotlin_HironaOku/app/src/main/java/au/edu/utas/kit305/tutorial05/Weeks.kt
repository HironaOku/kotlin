package au.edu.utas.kit305.tutorial05

data class Weeks(
        var week_name: String? = null,
        var marks_type: MutableList<String> = mutableListOf<String>()
        //var grades: MutableList<HashMap<String, String>> = mutableListOf<HashMap<String, String>>()
)
