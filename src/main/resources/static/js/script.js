var counter=0;
var max_fields      = 20; //maximum input boxes allowed
function add_more_field(){
    if(counter < max_fields) {
        counter+=1
        
        html='<div class="row" id="row'+counter+'">\
            <div class="col">\
                <input class="form-control mb-3" type="text" name="recipeMeasurement'+counter+'" maxlength="32" placeholder="Measurement" required>\
            </div>\
            <div class="col">\
                <input class="form-control mb-3" type="text" name="recipeIngredient'+counter+'" maxlength="16" placeholder="Ingredient" required>\
            </div>\
        </div>'

        var form = document.getElementById('ingredient_wrapper')

        form.innerHTML+=html
    }
}

function remove_field(){
    if(counter>0){
        var row_tag = document.getElementById('row'+counter)
        var form = document.getElementById('ingredient_wrapper')
        form.removeChild(row_tag)
        counter-=1
    }
}