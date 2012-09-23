require 'bitmap'
require 'color'
require 'graphicsBase'

module Gosu
  
  class Image
    
    def initialize(*args)
      case args.length
      #Argument is ImageData
      when 1
        initialize_1 args[0]
      when 2
        if args[1].class == Bitmap
          initialize_3_bitmap(args[0], args[1])
        else
          initialize_3_file_name(args[0], args[1])
        end
      when 3
        if args[1].class == Bitmap
          initialize_3_bitmap(args[0], args[1], args[2])
        else
          initialize_3_file_name(args[0], args[1], args[2])
        end        
      when 6
        if args[1].class == Bitmap
          initialize_7_bitmap(args[0], args[1], args[2], args[3], args[4], args[5])
        else
          initialize_7_file_name(args[0], args[1], args[2], args[3], args[4], args[5])
        end        
      when 7
        if args[1].class == Bitmap
          initialize_3_bitmap(args[0], args[1], args[2], args[3], args[4], args[5], args[6])
        else
          initialize_3_file_name(args[0], args[1], args[2], args[3], args[4], args[5], args[6])
        end         
      else
        raise ArgumentError
      end  
    end 
    
    #Private initialize methods
    private
    def initialize_1 data
      @data = data
    end
    
    def initialize_3_bitmap(graphics, source, tileable = false)
      initialize_7_bitmap(graphics, source, 0, 0, source.width, source.height, tileable)
    end
    
    def initialize_3_file_name(graphics, file_name, tileable = false)
      bmp = load_image_file(file_name)
      initialize_3_bitmap(graphics, bmp, tileable)
    end

    def initialize_7_file_name(graphics, file_name, src_x, src_y, src_width, src_height,
              tileable = false)
      bmp = load_image_file(file_name)
      initialize_7_bitmap(graphics, bmp, src_x, src_y, src_width, src_height, tileable)      
    end
    
    def initialize_7_bitmap(graphics, source, src_x, src_y, src_width, src_height,
              tileable = false)
      if tileable         
        @data = graphics.create_image(source, src_x, src_y, src_width, src_height, BF_TILEABLE)     
      else
        @data = graphics.create_image(source, src_x, src_y, src_width, src_height, BF_SMOOTH)  
      end   
    end
         
    public        
    def draw(x, y, z, factorX = 1, factorY = 1, c = Color::WHITE, mode = AM_DEFAULT) 
      
    end
            
  end
  
end