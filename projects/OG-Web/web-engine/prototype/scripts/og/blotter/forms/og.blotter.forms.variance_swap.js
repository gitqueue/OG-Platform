/**
 * Copyright 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * Please see distribution for license.
 */
$.register_module({
    name: 'og.blotter.forms.Variance_swap',
    dependencies: [],
    obj: function () {   
        return function () {
            var contructor = this;
            contructor.load = function () {
                var config = {}, dialog; 
                config.title = 'Varience Swap';
                var form = new og.common.util.ui.Form({
                    module: 'og.blotter.forms.variance_swap_tash',
                    data: {},
                    type_map: {},
                    selector: '.OG-blotter-form-block',
                    extras:{}
                });


                form.children.push(
                    new form.Block({
                        module: 'og.blotter.forms.block.swap_quick_entry_tash',
                        extras: {}
                    }),
                    new form.Block({
                        module: 'og.blotter.forms.block.swap_details_tash',
                        extras: {}
                    }),
                    new form.Block({
                        module: 'og.blotter.forms.block.swap_details_fixed_tash',
                        extras: {}
                    }) ,
                    new form.Block({
                        module: 'og.blotter.forms.block.swap_details_floating_tash',
                        extras: {}
                    })    
                );

                $('.OG-blotter-form-title').html(config.title);
                form.dom();
            }; 
            contructor.load();
            contructor.kill = function () {
                
            };
        };
    }
});